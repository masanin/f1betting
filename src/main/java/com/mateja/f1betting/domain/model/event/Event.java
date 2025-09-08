package com.mateja.f1betting.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private int eventId;
    private String eventName;
    private EventType eventType;
    private int year;
    private String country;
    private String location;
    private List<DriverMarket> driverMarkets;
}
