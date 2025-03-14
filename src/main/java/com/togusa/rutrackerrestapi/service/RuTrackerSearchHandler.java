package com.togusa.rutrackerrestapi.service;

import com.togusa.rutrackerrestapi.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RuTrackerSearchHandler extends AbstractTorrentSearchHandler implements ITorrentSearchHandler {

    private final RuTrackerSearchService search;

    @Override
    public GetTorrentsRs search(GetTorrentsRq request) {
        return processGetTorrentsRequest(request, search::search, "search");
    }

    @Override
    public GetTorrentsRs getBestOnes(GetTorrentsRq request) {
        return processGetTorrentsRequest(request, search::offerBestOnes, "best");
    }

    @Override
    public GetTorrentRs getById(GetTorrentRq request) {
        return processGetTorrentRequest(request, search::getById, "get");
    }
}
