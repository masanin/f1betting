package com.mateja.f1betting.domain.usecase;

import com.mateja.f1betting.domain.model.outcome.EventOutcome;
import com.mateja.f1betting.domain.model.outcome.EventOutcomeProcessingResult;
import reactor.core.publisher.Mono;

public interface ResultBetsUseCase {
    Mono<EventOutcomeProcessingResult> processEventOutcome(EventOutcome eventOutcomeResult);
}
