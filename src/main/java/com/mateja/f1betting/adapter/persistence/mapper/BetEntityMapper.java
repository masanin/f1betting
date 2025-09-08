package com.mateja.f1betting.adapter.persistence.mapper;

import com.mateja.f1betting.adapter.persistence.entity.BetEntity;
import com.mateja.f1betting.domain.model.bet.Bet;
import com.mateja.f1betting.domain.model.bet.BetStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BetEntityMapper {
    public static BetEntity toEntity(final Bet bet) {
        return BetEntity.builder()
                .betId(bet.getBetId())
                .userId(bet.getUserId())
                .eventId(bet.getEventId())
                .driverId(bet.getDriverId())
                .amount(bet.getAmount())
                .odds(bet.getOdds())
                .status(bet.getStatus().name())
                .potentialWinnings(bet.getPotentialWinnings())
                .placedAt(bet.getPlacedAt())
                .build();
    }

    public static Bet toDomain(final BetEntity entity) {
        return Bet.builder()
                .betId(entity.getBetId())
                .userId(entity.getUserId())
                .eventId(entity.getEventId())
                .driverId(entity.getDriverId())
                .amount(entity.getAmount())
                .odds(entity.getOdds())
                .status(BetStatus.valueOf(entity.getStatus()))
                .potentialWinnings(entity.getPotentialWinnings())
                .placedAt(entity.getPlacedAt())
                .build();
    }
}
