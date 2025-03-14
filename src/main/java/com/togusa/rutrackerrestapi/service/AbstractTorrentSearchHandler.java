package com.togusa.rutrackerrestapi.service;

import com.togusa.rutrackerrestapi.dto.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

@Slf4j
abstract class AbstractTorrentSearchHandler {

    <T, R> R processRequest(T request, Function<T, R> searchMethod, String action, String logMessage) {
        try {
            Instant start = Instant.now();
            R response = searchMethod.apply(request);
            Instant stop = Instant.now();
            log.info("[{}] {} in {}ms", action, logMessage, Duration.between(start, stop).toMillis());
            return response;
        } catch (Exception e) {
            log.error("[{}] Error processing request: {}", action, request, e);
            return null;
        }
    }

    GetTorrentsRs processGetTorrentsRequest(GetTorrentsRq request, Function<GetTorrentsRq, List<TorrentDto>> searchMethod, String action) {
        List<TorrentDto> torrents = processRequest(request, searchMethod, action, "Found torrents for '" + request.getSearchPhrase() + "'");
        return new GetTorrentsRs(torrents == null ? "Error occurred" : null, torrents);
    }

    GetTorrentRs processGetTorrentRequest(GetTorrentRq request, Function<GetTorrentRq, TorrentDto> searchMethod, String action) {
        TorrentDto torrent = processRequest(request, searchMethod, action, "Found torrent with id:" + request.getId());
        return new GetTorrentRs(torrent == null ? "Error occurred" : null, torrent);
    }
}