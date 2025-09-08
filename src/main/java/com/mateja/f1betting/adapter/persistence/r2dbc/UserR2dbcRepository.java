package com.mateja.f1betting.adapter.persistence.r2dbc;

import com.mateja.f1betting.adapter.persistence.entity.UserEntity;
import com.mateja.f1betting.adapter.persistence.mapper.UserEntityMapper;
import com.mateja.f1betting.domain.model.user.User;
import com.mateja.f1betting.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserR2dbcRepository implements UserRepository {
    private final DatabaseClient databaseClient;
    private final UserR2dbcCrudRepository crudRepository;

    @Override
    public Mono<User> insertUser(final User user) {
        log.debug("Inserting new user: {}", user.getUserId());
        return crudRepository.insertUser(user.getUserId(), user.getBalance())
                .then(Mono.just(user));
    }

    @Override
    public Mono<User> findById(final String id) {
        return crudRepository.findById(id)
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public Mono<Boolean> checkIfUserIdExists(final String userId) {
        return crudRepository.existsById(userId);
    }

    @Override
    public Mono<Boolean> deductBalanceIfSufficient(final String userId, final BigDecimal amount) {
        final String sql = """
                UPDATE users 
                SET balance = balance - $2
                WHERE user_id = $1 
                AND balance >= $2
                """;

        return databaseClient.sql(sql)
                .bind(0, userId)
                .bind(1, amount)
                .fetch()
                .rowsUpdated()
                .map(count -> count > 0)
                .doOnNext(success -> {
                    if (success) {
                        log.debug("Deducted {} from user {}", amount, userId);
                    } else {
                        log.warn("Failed to deduct {} from user {} - insufficient balance", amount, userId);
                    }
                });
    }

    @Override
    public Mono<Long> creditBalance(final Map<String, BigDecimal> userWinnings) {
        if (userWinnings.isEmpty()) {
            log.debug("No winnings to credit");
            return Mono.just(0L);
        }

        userWinnings.forEach((userId, amount) ->
                log.debug("Crediting {} EUR to user {}", amount, userId)
        );

        // Build single UPDATE query with CASE
        final StringBuilder sql = new StringBuilder("""
                UPDATE users 
                SET balance = balance + CASE user_id
                """);

        // Add CASE conditions for each user
        userWinnings.forEach((userId, amount) -> {
            sql.append(" WHEN '")
                    .append(userId)
                    .append("' THEN ")
                    .append(amount.toPlainString());
        });

        // Close CASE and add WHERE clause
        sql.append(" ELSE 0 END ");
        sql.append("WHERE user_id IN (");

        // Add user IDs to WHERE clause
        final String userIds = userWinnings.keySet().stream()
                .map(id -> "'" + id + "'")
                .collect(Collectors.joining(", "));
        sql.append(userIds);
        sql.append(")");

        log.debug("Executing batch update: {}", sql);

        // Execute the single UPDATE query
        return databaseClient.sql(sql.toString())
                .fetch()
                .rowsUpdated()
                .doOnNext(count -> {
                    if (count != userWinnings.size()) {
                        log.warn("Expected to update {} users but updated {}",
                                userWinnings.size(), count);
                    } else {
                        log.info("Successfully credited winnings to {} users in single query", count);
                    }
                });

    }
}

@Repository
interface UserR2dbcCrudRepository extends R2dbcRepository<UserEntity, String> {
    @Query("INSERT INTO users (user_id, balance) VALUES (:userId, :balance)")
    Mono<Void> insertUser(@Param("userId") String userId, @Param("balance") BigDecimal balance);
}