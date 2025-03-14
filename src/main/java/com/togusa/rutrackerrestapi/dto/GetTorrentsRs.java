package com.togusa.rutrackerrestapi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GetTorrentsRs extends TorrentBaseRs {

    private List<TorrentDto> torrents;

    public GetTorrentsRs(String description, List<TorrentDto> torrents) {
        super(description);
        this.torrents = torrents;
    }
}
