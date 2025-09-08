package com.mateja.f1betting.domain.service;

import com.mateja.f1betting.domain.exception.EventOutcomeAlreadyProcessedException;
import com.mateja.f1betting.domain.model.outcome.BetOutcomeResult;
import com.mateja.f1betting.domain.model.outcome.EventOutcome;
import com.mateja.f1betting.domain.repository.BetRepository;
import com.mateja.f1betting.domain.repository.EventOutcomeRepository;
import com.mateja.f1betting.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventOutcomeServiceTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventOutcomeRepository eventOutcomeRepository;

    @Mock
    private TransactionalOperator transactionalOperator;

    private EventOutcomeService eventOutcomeService;

    @BeforeEach
    void setUp() {
        eventOutcomeService = new EventOutcomeService(betRepository, userRepository, eventOutcomeRepository, transactionalOperator);
    }

    @Test
    @DisplayName("Should successfully process Event Outcome")
    void processEventOutcome_success() {
        // Given
        final EventOutcome outcome = new EventOutcome(1, 1);
        final long totalBetsUpdated = 3;
        final Map<String, BigDecimal> userWinnings = Map.of("user123", BigDecimal.ONE, "user456", BigDecimal.TEN);

        when(eventOutcomeRepository.insert(outcome))
                .thenReturn(Mono.just(outcome));
        when(betRepository.updateBetStatusAndCalculateWinners(outcome.getEventId(), outcome.getWinnerDriverId()))
                .thenReturn(Mono.just(new BetOutcomeResult(totalBetsUpdated, userWinnings)));
        when(userRepository.creditBalance(userWinnings))
                .thenReturn(Mono.just(1L));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // When & Then
        StepVerifier.create(eventOutcomeService.processEventOutcome(outcome))
                .assertNext(result -> {
                    assertThat(result.getEventId()).isEqualTo(outcome.getEventId());
                    assertThat(result.getWinnerDriverId()).isEqualTo(outcome.getWinnerDriverId());
                    assertThat(result.getTotalBetsUpdated()).isEqualTo(totalBetsUpdated);
                    assertThat(result.getTotalPayout()).isEqualByComparingTo(BigDecimal.valueOf(11L));
                })
                .verifyComplete();

        // Verify interactions
        verify(eventOutcomeRepository).insert(outcome);
        verify(betRepository).updateBetStatusAndCalculateWinners(outcome.getEventId(), outcome.getWinnerDriverId());
        verify(userRepository).creditBalance(userWinnings);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    @DisplayName("Should fail when Event Outcome insert fails")
    void processEventOutcome_EventOutcomeInsertFails() {
        // Given
        final EventOutcome outcome = new EventOutcome(1, 1);

        when(eventOutcomeRepository.insert(outcome))
                .thenReturn(Mono.error(new RuntimeException()));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // When & Then
        StepVerifier.create(eventOutcomeService.processEventOutcome(outcome))
                .expectError(EventOutcomeAlreadyProcessedException.class)
                .verify();

        // Verify interactions
        verify(eventOutcomeRepository).insert(outcome);
        verify(transactionalOperator).transactional(any(Mono.class));
        verifyNoInteractions(userRepository, betRepository);
    }

    @Test
    @DisplayName("Should fail when updateBetStatusAndCalculateWinners fails")
    void processEventOutcome_updateBetStatusAndCalculateWinnersFails() {
        // Given
        final EventOutcome outcome = new EventOutcome(1, 1);

        when(eventOutcomeRepository.insert(outcome))
                .thenReturn(Mono.just(outcome));
        when(betRepository.updateBetStatusAndCalculateWinners(outcome.getEventId(), outcome.getWinnerDriverId()))
                .thenReturn(Mono.error(new RuntimeException()));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // When & Then
        StepVerifier.create(eventOutcomeService.processEventOutcome(outcome))
                .expectError(RuntimeException.class)
                .verify();

        // Verify interactions
        verify(eventOutcomeRepository).insert(outcome);
        verify(betRepository).updateBetStatusAndCalculateWinners(outcome.getEventId(), outcome.getWinnerDriverId());
        verify(transactionalOperator).transactional(any(Mono.class));
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should fail when credit user balances fails")
    void processEventOutcome_creditBalanceFails() {
        // Given
        final EventOutcome outcome = new EventOutcome(1, 1);
        final long totalBetsUpdated = 3;
        final Map<String, BigDecimal> userWinnings = Map.of("user123", BigDecimal.ONE, "user456", BigDecimal.TEN);

        when(eventOutcomeRepository.insert(outcome))
                .thenReturn(Mono.just(outcome));
        when(betRepository.updateBetStatusAndCalculateWinners(outcome.getEventId(), outcome.getWinnerDriverId()))
                .thenReturn(Mono.just(new BetOutcomeResult(totalBetsUpdated, userWinnings)));
        when(userRepository.creditBalance(userWinnings))
                .thenReturn(Mono.error(new RuntimeException()));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // When & Then
        StepVerifier.create(eventOutcomeService.processEventOutcome(outcome))
                .expectError(RuntimeException.class)
                .verify();

        // Verify interactions
        verify(eventOutcomeRepository).insert(outcome);
        verify(betRepository).updateBetStatusAndCalculateWinners(outcome.getEventId(), outcome.getWinnerDriverId());
        verify(userRepository).creditBalance(userWinnings);
        verify(transactionalOperator).transactional(any(Mono.class));
    }
}
