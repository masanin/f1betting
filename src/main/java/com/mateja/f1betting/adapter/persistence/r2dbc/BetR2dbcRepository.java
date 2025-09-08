package com.mateja.f1betting.adapter.persistence.r2dbc;

import com.mateja.f1betting.adapter.persistence.entity.BetEntity;
import com.mateja.f1betting.adapter.persistence.mapper.BetEntityMapper;
import com.mateja.f1betting.domain.model.bet.Bet;
import com.mateja.f1betting.domain.model.outcome.BetOutcomeResult;
import com.mateja.f1betting.domain.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BetR2dbcRepository implements BetRepository {
    private final DatabaseClient databaseClient;
    private final BetR2dbcCrudRepository crudRepository;

    @Override
    public Mono<Bet> save(final Bet bet) {
        return crudRepository.save(BetEntityMapper.toEntity(bet))
                .map(BetEntityMapper::toDomain);
    }

    @Override
    public Flux<Bet> findByUserId(final String userId) {
        return crudRepository.findByUserId(userId)
                .map(BetEntityMapper::toDomain);
    }

    @Override
    public Mono<BetOutcomeResult> updateBetStatusAndCalculateWinners(final int eventId, final int winnerDriverId) {
        return updateAllBetStatuses(eventId, winnerDriverId)
                .flatMap(count -> getAggregatedWinnings(eventId).map(
                        winnings -> new BetOutcomeResult(count, winnings)
                ));
    }

    private Mono<Long> updateAllBetStatuses(final int eventId, final int winnerDriverId) {
        final String sql = """
                UPDATE bets
                SET status = CASE 
                    WHEN driver_id = $1 THEN 'WON'
                    ELSE 'LOST'
                END
                WHERE event_id = $2
                AND status = 'PENDING'
                """;
        return databaseClient.sql(sql)
                .bind(0, winnerDriverId)
                .bind(1, eventId)
                .fetch()
                .rowsUpdated();
    }

    private Mono<Map<String, BigDecimal>> getAggregatedWinnings(final int eventId) {
        final String sql = """
                SELECT user_id, SUM(amount * odds) as total_winnings
                FROM bets
                WHERE event_id = $1 
                AND status = 'WON'
                GROUP BY user_id
                """;
        return databaseClient.sql(sql)
                .bind(0, eventId)
                .map((row, metadata) -> Map.entry(
                        row.get("user_id", String.class),
                        row.get("total_winnings", BigDecimal.class)
                ))
                .all()
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .defaultIfEmpty(Collections.emptyMap());
    }
}

@Repository
interface BetR2dbcCrudRepository extends R2dbcRepository<BetEntity, String> {
    Flux<BetEntity> findByUserId(String userId);
}
