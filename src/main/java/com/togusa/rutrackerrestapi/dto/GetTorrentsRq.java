package com.togusa.rutrackerrestapi.dto;

import lombok.Data;
import org.springframework.lang.NonNull;

@Data
public class GetTorrentsRq {

    @NonNull
    private String searchPhrase;

    @NonNull
    private Boolean allPages;

}
