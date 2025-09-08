package com.mateja.f1betting.domain.service;

import com.mateja.f1betting.domain.exception.UserAlreadyExistsException;
import com.mateja.f1betting.domain.exception.UserNotFoundException;
import com.mateja.f1betting.domain.model.user.User;
import com.mateja.f1betting.domain.repository.UserRepository;
import com.mateja.f1betting.domain.usecase.UserManagementUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserManagementUseCase {
    private final UserRepository userRepository;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<User> getUserById(final String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id " + userId + " not found")));
    }

    @Override
    public Mono<User> registerUserIfNotExists(final String userId) {
        return userRepository.checkIfUserIdExists(userId)
                .flatMap(result ->
                        result ? Mono.error(new UserAlreadyExistsException(String.format("User with id %s already exists", userId)))
                                : userRepository.insertUser(User.createUserWithDefaultBalance(userId))
                )
                .as(transactionalOperator::transactional);
    }
}
