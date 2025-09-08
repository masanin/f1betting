package com.mateja.f1betting.adapter.external.openf1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpenF1SessionResponse {
    @JsonProperty("session_key")
    private Integer sessionKey;

    @JsonProperty("session_name")
    private String sessionName;

    @JsonProperty("session_type")
    private String sessionType;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("location")
    private String location;
}
