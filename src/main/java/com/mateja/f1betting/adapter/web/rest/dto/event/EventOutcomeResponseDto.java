package com.mateja.f1betting.adapter.web.rest.dto.event;

import java.math.BigDecimal;

public record EventOutcomeResponseDto(
        int sessionKey,
        int winnerDriverNumber,
        BigDecimal totalPayout,
        long totalBetsUpdated
) {
}
