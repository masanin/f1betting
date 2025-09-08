package com.mateja.f1betting.domain.usecase;

import com.mateja.f1betting.domain.model.event.Event;
import com.mateja.f1betting.domain.model.event.EventFilter;
import reactor.core.publisher.Flux;

public interface ViewEventsUseCase {
    Flux<Event> getEvents(EventFilter filter);
}
