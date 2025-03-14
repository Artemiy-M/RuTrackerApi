package com.togusa.rutrackerrestapi.service;

import com.togusa.rutrackerrestapi.dto.GetTorrentRq;
import com.togusa.rutrackerrestapi.dto.GetTorrentsRq;
import com.togusa.rutrackerrestapi.dto.TorrentDto;
import com.togusa.rutrackerrestapi.enums.TorrentStatus;
import com.togusa.rutrackerrestapi.mapper.TorrentMapper;
import com.togusa.rutrackerrestapi.service.auth.Authenticator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RuTrackerSearchService {

    @Value("${rutracker.search.url}")
    private String searchUrl;

    @Value("${rutracker.torrent.url}")
    private String torrentUrl;

    @Value("${app.proxy.use}")
    private boolean useProxy;

    private final Authenticator authenticator;
    private final HttpProxy httpProxy;
    private final TorrentMapper torrentMapper;

    public List<TorrentDto> search(GetTorrentsRq request) {
        if (!authenticateUser()) {
            log.warn("Authentication failed. Returning empty search results.");
            return Collections.emptyList();
        }

        log.info("Searching for phrase: '{}'", request.getSearchPhrase());
        List<Document> docs = fetchAllSearchPages(request.getSearchPhrase(), request.getAllPages());

        Set<String> uniqueIds = new HashSet<>();
        List<TorrentDto> searchResults = docs.stream()
                .flatMap(document -> extractTorrentsFromDocument(document).stream())
                .filter(torrent -> uniqueIds.add(torrent.getId()))
                .sorted(Comparator.comparing(TorrentDto::getDate).reversed())
                .collect(Collectors.toList());
        log.info("Found {} torrents for '{}'", searchResults.size(), request.getSearchPhrase());
        return searchResults;
    }

    public List<TorrentDto> offerBestOnes(GetTorrentsRq request) {
        List<TorrentDto> results = search(request);

        List<TorrentDto> filteredResults = results.stream()
                .filter(row -> row.getSeeders() > 0)
                .filter(row -> row.getStatus() != TorrentStatus.NOT_APPROVED && row.getStatus() != TorrentStatus.APPROVED)
                .sorted(Comparator.comparingInt(TorrentDto::getSeeders).reversed())
                .collect(Collectors.toList());

        if (filteredResults.isEmpty()) {
            return Collections.emptyList();
        }

        TorrentDto best = filteredResults.get(0);
        int topSeeders = best.getSeeders();

        List<TorrentDto> bestTorrents = filteredResults.stream()
                .filter(torrent -> torrent.getDate().after(best.getDate()) ||
                        (topSeeders - torrent.getSeeders()) < percentSeeders(topSeeders))
                .collect(Collectors.toList());

        return bestTorrents.isEmpty() ? List.of(best) : bestTorrents;
    }

    public TorrentDto getById(GetTorrentRq request) {
        if (!authenticateUser()) {
            log.warn("Authentication failed. Returning empty search results.");
            return null;
        }
        log.info("Getting torrent by ID: {}", request.getId());

        Document doc = fetchDocument(torrentUrl + request.getId(), useProxy).orElse(null);
        if (doc == null) {
            log.error("Failed to retrieve search results for torrent with id: {}", request.getId());
            return null;
        }

        TorrentDto searchResult = extractTorrentFromDocument(doc);
        searchResult.setId(request.getId());
        log.info("Found torrent with id {}", searchResult.getId());
        return searchResult;
    }

    private List<Document> fetchAllSearchPages(String searchPhrase, Boolean allPages) {
        List<Document> docs = new ArrayList<>();
        Document doc = fetchDocument(searchUrl + searchPhrase, useProxy).orElse(null);
        if (doc == null) {
            log.error("Failed to retrieve search results for '{}'", searchPhrase);
            return docs;
        }
        docs.add(doc);

        if (allPages) {
            List<String> searchPageUrls = getSearchPages(doc);
            for (String searchPageUrl : searchPageUrls) {
                fetchDocument(searchPageUrl, useProxy).ifPresent(docs::add);
            }
        }
        return docs;
    }

    private List<TorrentDto> extractTorrentsFromDocument(Document doc) {
        Elements results = doc.getElementsByClass("tCenter hl-tr");
        return results.stream()
                .map(torrentMapper::mapFromElement)
                .collect(Collectors.toList());
    }

    private TorrentDto extractTorrentFromDocument(Document doc) {
        return torrentMapper.mapFromDocument(doc);
    }

    private Optional<Document> fetchDocument(String url, boolean useProxy) {
        try {
            log.debug("Fetching document from URL: {}", url);
            return Optional.of(useProxy
                    ? Jsoup.connect(url).cookies(authenticator.getCookies()).proxy(httpProxy.getProxyObject()).get()
                    : Jsoup.connect(url).cookies(authenticator.getCookies()).get());
        } catch (IOException e) {
            log.error("Error fetching document from {}: {}", url, e.getMessage());
            return Optional.empty();
        }
    }

    private List<String> getSearchPages(Document doc) {
        List<String> pageLinks = new ArrayList<>();

        Elements pageElements = doc.select("p.small.bold a.pg");

        if (pageElements.isEmpty()) {
            return pageLinks;
        }

        for (Element pageElement : pageElements) {
            pageLinks.add(pageElement.absUrl("href"));
        }
        return pageLinks;
    }

    private boolean authenticateUser() {
        if (!authenticator.isAuthenticated()) {
            log.info("User not authenticated. Attempting login...");
            authenticator.login();
        }
        return authenticator.isAuthenticated();
    }

    private int percentSeeders(int seedersAmount) {
        return (int) ((double) seedersAmount / 100 * 15);
    }
}

