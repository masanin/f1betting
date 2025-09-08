package com.mateja.f1betting.domain.usecase;

import com.mateja.f1betting.domain.model.user.User;
import reactor.core.publisher.Mono;

public interface UserManagementUseCase {
    Mono<User> getUserById(String userId);

    Mono<User> registerUserIfNotExists(String userId);
}
