package com.togusa.rutrackerrestapi.controllers;

import com.togusa.rutrackerrestapi.dto.*;
import com.togusa.rutrackerrestapi.service.ITorrentSearchHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.function.Function;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/torrents")
public class TorrentApiController {

    private final ITorrentSearchHandler torrentSearchService;

    @PostMapping("/search")
    public ResponseEntity<GetTorrentsRs> search(@Validated @RequestBody GetTorrentsRq request) {
        return processRequest("search", request, torrentSearchService::search);
    }

    @PostMapping("/best")
    public ResponseEntity<GetTorrentsRs> getBestOnes(@Validated @RequestBody GetTorrentsRq request) {
        return processRequest("best", request, torrentSearchService::getBestOnes);
    }

    @PostMapping("/get")
    public ResponseEntity<GetTorrentRs> getById(@Validated @RequestBody GetTorrentRq request) {
        return processRequest("get", request, torrentSearchService::getById);
    }

    @GetMapping("/checkalive")
    public ResponseEntity<Boolean> checkAlive() {
        log.debug("Health check requested.");
        return ResponseEntity.ok(true);
    }

    private <T, R extends TorrentBaseRs> ResponseEntity<R> processRequest(String action, T request, Function<T, R> serviceMethod) {
        log.info("Processing '{}' request: {}", action, request);
        R response = serviceMethod.apply(request);
        HttpStatus status = response.getDescription() == null || response.getDescription().isEmpty() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        log.info("Returning response for '{}': status={}, description={}", action, status, response.getDescription());
        return ResponseEntity.status(status).body(response);
    }
}