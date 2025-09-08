package com.mateja.f1betting.domain.repository;

import com.mateja.f1betting.domain.model.bet.Bet;
import com.mateja.f1betting.domain.model.outcome.BetOutcomeResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BetRepository {
    Mono<Bet> save(Bet bet);

    Flux<Bet> findByUserId(String userId);

    Mono<BetOutcomeResult> updateBetStatusAndCalculateWinners(int eventId, int winnerDriverId);
}
