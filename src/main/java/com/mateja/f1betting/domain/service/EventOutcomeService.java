package com.mateja.f1betting.domain.service;

import com.mateja.f1betting.domain.exception.EventOutcomeAlreadyProcessedException;
import com.mateja.f1betting.domain.model.outcome.EventOutcome;
import com.mateja.f1betting.domain.model.outcome.EventOutcomeProcessingResult;
import com.mateja.f1betting.domain.repository.BetRepository;
import com.mateja.f1betting.domain.repository.EventOutcomeRepository;
import com.mateja.f1betting.domain.repository.UserRepository;
import com.mateja.f1betting.domain.usecase.ResultBetsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventOutcomeService implements ResultBetsUseCase {
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final EventOutcomeRepository eventOutcomeRepository;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<EventOutcomeProcessingResult> processEventOutcome(final EventOutcome outcome) {
        return insertOutcome(outcome)
                .flatMap(result -> betRepository.updateBetStatusAndCalculateWinners(result.getEventId(), result.getWinnerDriverId()))
                .flatMap(result ->
                        userRepository.creditBalance(result.userWinnings())
                                .map(_ ->
                                        EventOutcomeProcessingResult.builder()
                                                .eventId(outcome.getEventId())
                                                .winnerDriverId(outcome.getWinnerDriverId())
                                                .totalBetsUpdated(result.totalBetsUpdated())
                                                .totalPayout(result.userWinnings().values().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                                                .build()
                                )
                )
                .as(transactionalOperator::transactional);
    }

    private Mono<EventOutcome> insertOutcome(final EventOutcome outcome) {
        return eventOutcomeRepository.insert(outcome)
                .onErrorMap(_ -> new EventOutcomeAlreadyProcessedException(String.format("Event outcome with event id %d already processed", outcome.getEventId())));
    }
}
