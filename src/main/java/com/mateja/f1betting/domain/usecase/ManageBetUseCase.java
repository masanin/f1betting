package com.mateja.f1betting.domain.usecase;

import com.mateja.f1betting.domain.model.bet.Bet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ManageBetUseCase {
    Mono<Bet> placeBet(PlaceBetCommand command);

    Flux<Bet> getBets(String userId);

    record PlaceBetCommand(
            String userId,
            int eventId,
            int driverId,
            BigDecimal amount
    ) {
    }
}
