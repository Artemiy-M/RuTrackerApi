package com.togusa.rutrackerrestapi.dto;

import com.togusa.rutrackerrestapi.enums.TorrentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TorrentDto {
    private String id;
    private final TorrentStatus status;
    private final String forum;
    private final String name;
    private final String URL;
    private final String author;
    private final double size; // size in GB
    private final int seeders;
    private final int leechers;
    private final int timesDownloaded;
    private final Date date;
    private String torrentMagnetURL;
}
