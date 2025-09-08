package com.mateja.f1betting.adapter.web.rest.mapper;

import com.mateja.f1betting.adapter.web.rest.dto.event.EventOutcomeRequestDto;
import com.mateja.f1betting.adapter.web.rest.dto.event.EventOutcomeResponseDto;
import com.mateja.f1betting.domain.model.outcome.EventOutcome;
import com.mateja.f1betting.domain.model.outcome.EventOutcomeProcessingResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventOutcomeMapper {
    public static EventOutcome map(final EventOutcomeRequestDto request) {
        return EventOutcome.builder()
                .eventId(request.sessionKey())
                .winnerDriverId(request.winnerDriverNumber())
                .build();
    }

    public static EventOutcomeResponseDto map(final EventOutcomeProcessingResult result) {
        return new EventOutcomeResponseDto(
                result.getEventId(),
                result.getWinnerDriverId(),
                result.getTotalPayout(),
                result.getTotalBetsUpdated()
        );
    }
}
