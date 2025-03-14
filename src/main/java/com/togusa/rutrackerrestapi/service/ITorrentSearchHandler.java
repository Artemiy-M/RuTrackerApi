package com.togusa.rutrackerrestapi.service;

import com.togusa.rutrackerrestapi.dto.GetTorrentRq;
import com.togusa.rutrackerrestapi.dto.GetTorrentRs;
import com.togusa.rutrackerrestapi.dto.GetTorrentsRq;
import com.togusa.rutrackerrestapi.dto.GetTorrentsRs;

public interface ITorrentSearchHandler {

    GetTorrentsRs search(GetTorrentsRq request);
    GetTorrentsRs getBestOnes(GetTorrentsRq request);
    GetTorrentRs getById(GetTorrentRq request);

}
