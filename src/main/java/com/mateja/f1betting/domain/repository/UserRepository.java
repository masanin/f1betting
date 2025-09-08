package com.mateja.f1betting.domain.repository;

import com.mateja.f1betting.domain.model.user.User;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

public interface UserRepository {

    Mono<User> insertUser(User user);

    Mono<User> findById(String id);

    Mono<Boolean> checkIfUserIdExists(String userId);

    Mono<Boolean> deductBalanceIfSufficient(String userId, BigDecimal amount);

    Mono<Long> creditBalance(Map<String, BigDecimal> userWinnings);
}
