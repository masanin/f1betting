package com.mateja.f1betting.domain.repository;

import com.mateja.f1betting.domain.model.outcome.EventOutcome;
import reactor.core.publisher.Mono;

public interface EventOutcomeRepository {
    Mono<EventOutcome> insert(EventOutcome outcome);

    Mono<Boolean> existByEventId(int eventId);
}
