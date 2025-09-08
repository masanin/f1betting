package com.mateja.f1betting.adapter.web.rest.dto.event;

import java.util.List;

public record EventResponseDto(
        int eventId,
        String eventName,
        EventTypeDto eventType,
        int year,
        String country,
        String location,
        List<DriverMarketResponseDto> driverMarkets
) {
}
