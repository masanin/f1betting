package com.mateja.f1betting.domain.service;

import com.mateja.f1betting.domain.exception.DriverMarketNotFoundException;
import com.mateja.f1betting.domain.exception.EventOutcomeExistsException;
import com.mateja.f1betting.domain.exception.InsufficientBalanceException;
import com.mateja.f1betting.domain.integration.F1EventProvider;
import com.mateja.f1betting.domain.model.bet.Bet;
import com.mateja.f1betting.domain.model.event.DriverMarket;
import com.mateja.f1betting.domain.repository.BetRepository;
import com.mateja.f1betting.domain.repository.EventOutcomeRepository;
import com.mateja.f1betting.domain.repository.UserRepository;
import com.mateja.f1betting.domain.usecase.ManageBetUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BettingService implements ManageBetUseCase {
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final EventOutcomeRepository eventOutcomeRepository;
    private final F1EventProvider f1EventProvider;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<Bet> placeBet(final ManageBetUseCase.PlaceBetCommand command) {
        log.info("Placing bet for user: {} on event: {}, driver: {}, amount: {} EUR",
                command.userId(), command.eventId(), command.driverId(), command.amount());

        return checkIfEventOutcomeExists(command)
                .then(Mono.defer(() -> depositAmountForCommand(command)))
                .then(Mono.defer(() -> fetchDriverByEventIdAndDriverId(command.eventId(), command.driverId())))
                .flatMap(driverMarket -> betRepository.save(createBet(command, driverMarket)))
                .as(transactionalOperator::transactional)
                .doOnSuccess(bet -> log.info("Bet placed successfully: {}", bet.getBetId()))
                .doOnError(e -> log.error("Error placing bet: ", e));
    }

    @Override
    public Flux<Bet> getBets(final String userId) {
        return betRepository.findByUserId(userId);
    }


    private Mono<Void> depositAmountForCommand(final ManageBetUseCase.PlaceBetCommand command) {
        return userRepository.deductBalanceIfSufficient(command.userId(), command.amount())
                .flatMap(result -> result
                        ? Mono.empty()
                        : Mono.error(new InsufficientBalanceException(String.format("Cannot deduct %s from user with id %s", command.amount(), command.userId()))));
    }

    private Mono<Void> checkIfEventOutcomeExists(final ManageBetUseCase.PlaceBetCommand command) {
        return eventOutcomeRepository.existByEventId(command.eventId())
                .flatMap(result -> result
                        ? Mono.error(new EventOutcomeExistsException("Event outcome already exists for event id " + command.eventId()))
                        : Mono.empty()
                );
    }

    private Mono<DriverMarket> fetchDriverByEventIdAndDriverId(final int eventId, final int driverId) {
        return f1EventProvider.fetchF1DriverMarket(eventId, driverId)
                .onErrorMap(DriverMarketNotFoundException::new);
    }

    private static Bet createBet(final ManageBetUseCase.PlaceBetCommand command, final DriverMarket driverMarket) {
        return Bet.createBet(command.userId(), command.eventId(), command.driverId(), command.amount(), driverMarket.getOdds());
    }
}
