package com.togusa.rutrackerrestapi.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GetTorrentRs extends TorrentBaseRs {

    private TorrentDto torrents;

    public GetTorrentRs(String description, TorrentDto torrents) {
        super(description);
        this.torrents = torrents;
    }
}
