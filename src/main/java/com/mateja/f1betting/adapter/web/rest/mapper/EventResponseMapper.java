package com.mateja.f1betting.adapter.web.rest.mapper;

import com.mateja.f1betting.adapter.web.rest.dto.event.DriverMarketResponseDto;
import com.mateja.f1betting.adapter.web.rest.dto.event.EventResponseDto;
import com.mateja.f1betting.adapter.web.rest.dto.event.EventTypeDto;
import com.mateja.f1betting.domain.model.event.DriverMarket;
import com.mateja.f1betting.domain.model.event.Event;
import com.mateja.f1betting.domain.model.event.EventType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventResponseMapper {
    public static EventResponseDto map(final Event f1Event) {
        return new EventResponseDto(
                f1Event.getEventId(),
                f1Event.getEventName(),
                map(f1Event.getEventType()),
                f1Event.getYear(),
                f1Event.getCountry(),
                f1Event.getLocation(),
                map(f1Event.getDriverMarkets())
        );
    }

    private static EventTypeDto map(final EventType eventType) {
        return EventTypeDto.valueOf(eventType.name());
    }

    private static List<DriverMarketResponseDto> map(final List<DriverMarket> driverMarkets) {
        return driverMarkets.stream()
                .map(driverMarket ->
                        new DriverMarketResponseDto(
                                driverMarket.getDriverId(),
                                driverMarket.getFullName(),
                                driverMarket.getOdds()))
                .toList();
    }
}
