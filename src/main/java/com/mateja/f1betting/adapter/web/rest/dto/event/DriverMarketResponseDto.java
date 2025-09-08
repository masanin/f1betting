package com.mateja.f1betting.adapter.web.rest.dto.event;

import java.math.BigDecimal;

public record DriverMarketResponseDto(
        Integer driverId,
        String fullName,
        BigDecimal odds
) {
}
