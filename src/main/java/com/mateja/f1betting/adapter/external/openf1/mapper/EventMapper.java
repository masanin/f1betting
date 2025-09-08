package com.mateja.f1betting.adapter.external.openf1.mapper;

import com.mateja.f1betting.adapter.external.openf1.dto.OpenF1DriverResponse;
import com.mateja.f1betting.adapter.external.openf1.dto.OpenF1SessionResponse;
import com.mateja.f1betting.domain.model.event.Event;
import com.mateja.f1betting.domain.model.event.EventType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static Event toF1Event(final OpenF1SessionResponse f1Event, final Collection<OpenF1DriverResponse> drivers) {
        return Event.builder()
                .eventId(f1Event.getSessionKey())
                .eventName(f1Event.getSessionName())
                .eventType(EventType.valueOf(f1Event.getSessionType()))
                .year(f1Event.getYear())
                .country(f1Event.getCountryName())
                .location(f1Event.getLocation())
                .driverMarkets(F1DriverMarketMapper.toF1DriverMarkets(drivers))
                .build();
    }
}
