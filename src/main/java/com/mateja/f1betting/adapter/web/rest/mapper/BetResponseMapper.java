package com.mateja.f1betting.adapter.web.rest.mapper;

import com.mateja.f1betting.adapter.web.rest.dto.bet.BetResponseDto;
import com.mateja.f1betting.adapter.web.rest.dto.bet.BetStatusDto;
import com.mateja.f1betting.domain.model.bet.Bet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BetResponseMapper {

    public static BetResponseDto map(final Bet bet) {
        return new BetResponseDto(
                bet.getBetId(),
                bet.getUserId(),
                bet.getEventId(),
                bet.getDriverId(),
                bet.getAmount(),
                bet.getOdds(),
                bet.getPotentialWinnings(),
                BetStatusDto.valueOf(bet.getStatus().name()),
                bet.getPlacedAt()
        );
    }
}
