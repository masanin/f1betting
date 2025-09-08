package com.mateja.f1betting.domain.service;

import com.mateja.f1betting.domain.exception.UserAlreadyExistsException;
import com.mateja.f1betting.domain.exception.UserNotFoundException;
import com.mateja.f1betting.domain.model.user.User;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionalOperator transactionalOperator;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, transactionalOperator);
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_Success() {
        // Given
        final String userId = "user123";
        final User expectedUser = new User(userId, new BigDecimal("75.50"));

        when(userRepository.findById(userId))
                .thenReturn(Mono.just(expectedUser));

        // When & Then
        StepVerifier.create(userService.getUserById(userId))
                .assertNext(user -> {
                    assertThat(user.getUserId()).isEqualTo(userId);
                    assertThat(user.getBalance()).isEqualByComparingTo(new BigDecimal("75.50"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user doesn't exist")
    void getUserById_NotFound() {
        // Given
        final String userId = "nonexistent";

        when(userRepository.findById(userId))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userService.getUserById(userId))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                                throwable.getMessage().contains("User with id nonexistent not found")
                )
                .verify();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void registerUserIfNotExists_NewUser_Success() {
        // Given
        final String userId = "newUser";
        final User newUser = User.createUserWithDefaultBalance(userId);

        when(userRepository.checkIfUserIdExists(userId))
                .thenReturn(Mono.just(false));
        when(userRepository.insertUser(any(User.class)))
                .thenReturn(Mono.just(newUser));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When & Then
        StepVerifier.create(userService.registerUserIfNotExists(userId))
                .assertNext(user -> {
                    assertThat(user.getUserId()).isEqualTo(userId);
                    assertThat(user.getBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
                })
                .verifyComplete();

        verify(userRepository).insertUser(argThat(user ->
                user.getUserId().equals(userId) &&
                        user.getBalance().compareTo(new BigDecimal("100.00")) == 0
        ));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when user exists")
    void registerUserIfNotExists_ExistingUser_Failure() {
        // Given
        final String userId = "existingUser";

        when(userRepository.checkIfUserIdExists(userId))
                .thenReturn(Mono.just(true));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When & Then
        StepVerifier.create(userService.registerUserIfNotExists(userId))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserAlreadyExistsException &&
                                throwable.getMessage().contains("User with id existingUser already exists")
                )
                .verify();

        verify(userRepository, never()).insertUser(any());
    }
}