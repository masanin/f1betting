package com.mateja.f1betting.adapter.external.openf1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpenF1DriverResponse {
    @JsonProperty("driver_number")
    private Integer driverNumber;

    @JsonProperty("session_key")
    private Integer sessionKey;

    @JsonProperty("full_name")
    private String fullName;
}
