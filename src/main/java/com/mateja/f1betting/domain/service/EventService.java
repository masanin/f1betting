package com.mateja.f1betting.domain.service;

import com.mateja.f1betting.domain.integration.F1EventProvider;
import com.mateja.f1betting.domain.model.event.Event;
import com.mateja.f1betting.domain.model.event.EventFilter;
import com.mateja.f1betting.domain.usecase.ViewEventsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService implements ViewEventsUseCase {
    private final F1EventProvider f1EventProvider;

    @Override
    public Flux<Event> getEvents(final EventFilter filter) {
        log.info("Fetching events with filter: {}", filter);
        return f1EventProvider.fetchF1Events(filter)
                .doOnNext(event -> log.debug("Found event: {}", event.getEventName()))
                .doOnComplete(() -> log.info("Finished fetching events"));
    }
}
