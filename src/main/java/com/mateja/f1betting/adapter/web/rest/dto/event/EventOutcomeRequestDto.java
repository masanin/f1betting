package com.mateja.f1betting.adapter.web.rest.dto.event;

import jakarta.validation.constraints.NotNull;

public record EventOutcomeRequestDto(
        @NotNull(message = "Session Key is required")
        Integer sessionKey,
        @NotNull(message = "Winner Driver Number is required")
        Integer winnerDriverNumber
) {
}
