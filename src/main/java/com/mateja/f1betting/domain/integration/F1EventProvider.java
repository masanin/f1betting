package com.mateja.f1betting.domain.integration;

import com.mateja.f1betting.domain.model.event.DriverMarket;
import com.mateja.f1betting.domain.model.event.Event;
import com.mateja.f1betting.domain.model.event.EventFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface F1EventProvider {
    Flux<Event> fetchF1Events(EventFilter filter);

    Mono<DriverMarket> fetchF1DriverMarket(int eventId, int driverId);
}
