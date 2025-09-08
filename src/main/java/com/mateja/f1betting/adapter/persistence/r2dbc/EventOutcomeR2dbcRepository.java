package com.mateja.f1betting.adapter.persistence.r2dbc;

import com.mateja.f1betting.adapter.persistence.entity.EventOutcomeEntity;
import com.mateja.f1betting.domain.model.outcome.EventOutcome;
import com.mateja.f1betting.domain.repository.EventOutcomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventOutcomeR2dbcRepository implements EventOutcomeRepository {
    private final EventOutcomeR2dbcCrudRepository crudRepository;

    @Override
    public Mono<EventOutcome> insert(final EventOutcome outcome) {
        log.debug("Inserting new event outcome: {} - {}", outcome.getEventId(), outcome.getWinnerDriverId());
        return crudRepository.insertEventOutcome(outcome.getEventId(), outcome.getWinnerDriverId())
                .then(Mono.just(outcome));
    }

    @Override
    public Mono<Boolean> existByEventId(final int eventId) {
        return crudRepository.existsById(eventId);
    }
}

@Repository
interface EventOutcomeR2dbcCrudRepository extends R2dbcRepository<EventOutcomeEntity, Integer> {
    @Query("INSERT INTO event_outcomes (event_id, winner_driver_id) VALUES (:eventId, :winnerDriverId)")
    Mono<Void> insertEventOutcome(@Param("eventId") Integer eventId, @Param("winnerDriverId") Integer winnerDriverId);
}