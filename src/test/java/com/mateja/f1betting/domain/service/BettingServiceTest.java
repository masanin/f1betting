package com.mateja.f1betting.domain.service;

import com.mateja.f1betting.domain.exception.DriverMarketNotFoundException;
import com.mateja.f1betting.domain.exception.EventOutcomeExistsException;
import com.mateja.f1betting.domain.integration.F1EventProvider;
import com.mateja.f1betting.domain.model.bet.Bet;
import com.mateja.f1betting.domain.model.bet.BetStatus;
import com.mateja.f1betting.domain.model.event.DriverMarket;
import com.mateja.f1betting.domain.repository.BetRepository;
import com.mateja.f1betting.domain.repository.EventOutcomeRepository;
import com.mateja.f1betting.domain.repository.UserRepository;
import com.mateja.f1betting.domain.usecase.ManageBetUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BettingServiceTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventOutcomeRepository eventOutcomeRepository;

    @Mock
    private F1EventProvider f1EventProvider;

    @Mock
    private TransactionalOperator transactionalOperator;

    private BettingService bettingService;

    @BeforeEach
    void setUp() {
        bettingService = new BettingService(
                betRepository,
                userRepository,
                eventOutcomeRepository,
                f1EventProvider,
                transactionalOperator
        );
    }

    @Test
    @DisplayName("Should successfully place bet when all conditions are met")
    void placeBet_Success() {
        // Given
        final String userId = "user123";
        final int eventId = 1234;
        final int driverId = 1;
        final BigDecimal amount = new BigDecimal("50.00");
        final BigDecimal odds = new BigDecimal("2.0");

        final ManageBetUseCase.PlaceBetCommand command =
                new ManageBetUseCase.PlaceBetCommand(userId, eventId, driverId, amount);

        final DriverMarket driverMarket = new DriverMarket(driverId, "Max Verstappen", odds);
        final Bet expectedBet = Bet.createBet(userId, eventId, driverId, amount, odds);

        when(f1EventProvider.fetchF1DriverMarket(eventId, driverId))
                .thenReturn(Mono.just(driverMarket));
        when(userRepository.deductBalanceIfSufficient(userId, amount))
                .thenReturn(Mono.just(true));
        when(eventOutcomeRepository.existByEventId(eventId))
                .thenReturn(Mono.just(false));
        when(betRepository.save(any(Bet.class)))
                .thenReturn(Mono.just(expectedBet));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // When & Then
        StepVerifier.create(bettingService.placeBet(command))
                .assertNext(bet -> {
                    assertThat(bet.getUserId()).isEqualTo(userId);
                    assertThat(bet.getEventId()).isEqualTo(eventId);
                    assertThat(bet.getDriverId()).isEqualTo(driverId);
                    assertThat(bet.getAmount()).isEqualTo(amount);
                    assertThat(bet.getOdds()).isEqualTo(odds);
                    assertThat(bet.getStatus()).isEqualTo(BetStatus.PENDING);
                })
                .verifyComplete();

        // Verify interactions
        verify(f1EventProvider).fetchF1DriverMarket(eventId, driverId);
        verify(userRepository).deductBalanceIfSufficient(userId, amount);
        verify(eventOutcomeRepository).existByEventId(eventId);
        verify(betRepository).save(any(Bet.class));
    }

    @Test
    @DisplayName("Should fail when driver market not found")
    void placeBet_DriverNotFound() {
        // Given
        final ManageBetUseCase.PlaceBetCommand command =
                new ManageBetUseCase.PlaceBetCommand("user123", 1234, 999, new BigDecimal("50.00"));

        when(eventOutcomeRepository.existByEventId(command.eventId()))
                .thenReturn(Mono.just(false));
        when(userRepository.deductBalanceIfSufficient(command.userId(), command.amount()))
                .thenReturn(Mono.just(true));
        when(f1EventProvider.fetchF1DriverMarket(1234, 999))
                .thenReturn(Mono.error(new RuntimeException("Driver not found")));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // When & Then
        StepVerifier.create(bettingService.placeBet(command))
                .expectError(DriverMarketNotFoundException.class)
                .verify();

        // Verify no further operations were attempted
        verifyNoInteractions(betRepository);
    }

    @Test
    @DisplayName("Should fail when user has insufficient balance")
    void placeBet_InsufficientBalance() {
        // Given
        final String userId = "user123";
        final int eventId = 1234;
        final int driverId = 1;
        final BigDecimal amount = new BigDecimal("150.00");

        final ManageBetUseCase.PlaceBetCommand command =
                new ManageBetUseCase.PlaceBetCommand(userId, eventId, driverId, amount);

        when(eventOutcomeRepository.existByEventId(eventId))
                .thenReturn(Mono.just(false));
        when(userRepository.deductBalanceIfSufficient(userId, amount))
                .thenReturn(Mono.just(false));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // When & Then
        StepVerifier.create(bettingService.placeBet(command))
                .expectErrorMessage("Cannot deduct 150.00 from user with id user123")
                .verify();

        // Verify bet was not saved
        verifyNoInteractions(betRepository, f1EventProvider);
    }

    @Test
    @DisplayName("Should fail when event outcome already exists")
    void placeBet_EventOutcomeExists() {
        // Given
        final String userId = "user123";
        final int eventId = 1234;
        final int driverId = 1;
        final BigDecimal amount = new BigDecimal("50.00");

        final ManageBetUseCase.PlaceBetCommand command =
                new ManageBetUseCase.PlaceBetCommand(userId, eventId, driverId, amount);

        when(eventOutcomeRepository.existByEventId(eventId))
                .thenReturn(Mono.just(true));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // When & Then
        StepVerifier.create(bettingService.placeBet(command))
                .expectError(EventOutcomeExistsException.class)
                .verify();

        // Verify only the outcome check was called, nothing else
        verify(eventOutcomeRepository).existByEventId(eventId);
        verifyNoInteractions(userRepository, f1EventProvider, betRepository);
    }

    @Test
    @DisplayName("Should return user bets")
    void getBets_Success() {
        // Given
        final String userId = "user123";
        final Bet bet1 = createTestBet(userId, 1234, 1, "50.00", "2.0");
        final Bet bet2 = createTestBet(userId, 5678, 2, "30.00", "3.0");

        when(betRepository.findByUserId(userId))
                .thenReturn(Flux.just(bet1, bet2));

        // When & Then
        StepVerifier.create(bettingService.getBets(userId))
                .assertNext(bet -> {
                    assertThat(bet.getUserId()).isEqualTo(userId);
                    assertThat(bet.getEventId()).isEqualTo(1234);
                })
                .assertNext(bet -> {
                    assertThat(bet.getUserId()).isEqualTo(userId);
                    assertThat(bet.getEventId()).isEqualTo(5678);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty flux when user has no bets")
    void getBets_NoBets() {
        // Given
        final String userId = "user123";
        when(betRepository.findByUserId(userId))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(bettingService.getBets(userId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should capture correct bet parameters when saving")
    void placeBet_VerifyBetParameters() {
        // Given
        final String userId = "user123";
        final int eventId = 1234;
        final int driverId = 1;
        final BigDecimal amount = new BigDecimal("75.00");
        final BigDecimal odds = new BigDecimal("3.0");

        final ManageBetUseCase.PlaceBetCommand command =
                new ManageBetUseCase.PlaceBetCommand(userId, eventId, driverId, amount);

        final DriverMarket driverMarket = new DriverMarket(driverId, "Lewis Hamilton", odds);

        when(f1EventProvider.fetchF1DriverMarket(eventId, driverId))
                .thenReturn(Mono.just(driverMarket));
        when(userRepository.deductBalanceIfSufficient(userId, amount))
                .thenReturn(Mono.just(true));
        when(eventOutcomeRepository.existByEventId(eventId))
                .thenReturn(Mono.just(false));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        final ArgumentCaptor<Bet> betCaptor = ArgumentCaptor.forClass(Bet.class);
        when(betRepository.save(betCaptor.capture()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // When
        StepVerifier.create(bettingService.placeBet(command))
                .expectNextCount(1)
                .verifyComplete();

        // Then
        final Bet capturedBet = betCaptor.getValue();
        assertThat(capturedBet.getUserId()).isEqualTo(userId);
        assertThat(capturedBet.getEventId()).isEqualTo(eventId);
        assertThat(capturedBet.getDriverId()).isEqualTo(driverId);
        assertThat(capturedBet.getAmount()).isEqualTo(amount);
        assertThat(capturedBet.getOdds()).isEqualTo(odds);
        assertThat(capturedBet.getStatus()).isEqualTo(BetStatus.PENDING);
    }

    // Helper method
    private static Bet createTestBet(final String userId, final int eventId, final int driverId, final String amount, final String odds) {
        return Bet.createBet(userId, eventId, driverId,
                new BigDecimal(amount), new BigDecimal(odds));
    }
}