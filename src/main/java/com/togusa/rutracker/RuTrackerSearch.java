package com.togusa.rutracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RuTrackerSearch {

    public RuTrackerSearch(String login, String password) {
        if (Settings.USE_PROXY) {
            HttpProxy.setProxy();
        }

        Settings.TRACKER_LOGIN = login;
        Settings.TRACKER_PASSWORD = password;
    }

    public List<Torrent> search(String searchPhrase) throws IOException {
        if (!Authenticator.isAuthenticated()) {
            Authenticator.login();
        }

        if (Authenticator.isAuthenticated()) {
            Document doc;
            if (Settings.USE_PROXY) {
                doc = Jsoup.connect(Settings.SEARCH_URL + searchPhrase)
                        .cookies(Authenticator.getCookies())
                        .proxy(HttpProxy.getProxyObject())
                        .get();
            } else {
                doc = Jsoup.connect(Settings.SEARCH_URL + searchPhrase)
                        .cookies(Authenticator.getCookies())
                        .get();
            }

            Elements results = doc.getElementsByClass("tCenter hl-tr");
            List<Torrent> searchResults = new ArrayList<>();
            for (Element result : results) {
                searchResults.add(new Torrent(result));
            }
            return searchResults;
        }
        return Collections.emptyList();
    }

    // trying to choose best result based on date, amount of seeds and status of the torrent
    public Torrent offerBestOne(List<Torrent> results) {
        if (results.size() > 0) {
            List<Torrent> filteredResults = results.stream().
                    filter(row -> row.getSeeders() > 0).
                    filter(row -> !row.getStatus().equals(TorrentStatus.NOT_APPROVED) || !row.getStatus().equals(TorrentStatus.APPROVED)).
                    sorted(Comparator.comparingInt(Torrent::getSeeders).reversed()).
                    collect(Collectors.toList());
            if (filteredResults.size() > 0) {
                Torrent currentResult = filteredResults.get(0);
                int topSeedsAmount = currentResult.getSeeders();
                if (filteredResults.size() > 1) {
                    for (int i = 1; i < filteredResults.size(); i++) {
                        if (filteredResults.get(i).getDate().after(currentResult.getDate()) &&
                                (filteredResults.get(0).getSeeders() - filteredResults.get(i).getSeeders()) < percentSeeders(topSeedsAmount) &&
                                filteredResults.get(i).getSeeders() > 0) {
                            currentResult = filteredResults.get(i);
                        }
                    }
                }
                return currentResult;
            }
            return results.get(0);
        } else {
            return null;
        }
    }

    // 15 percent difference in seeders counts result as qualifier to best offering
    private int percentSeeders(int seedersAmount) {
        double result =  ((double) seedersAmount / 100) * 15;
        return (int) result;
    }
}
