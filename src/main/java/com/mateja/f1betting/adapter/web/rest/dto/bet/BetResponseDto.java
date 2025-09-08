package com.mateja.f1betting.adapter.web.rest.dto.bet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BetResponseDto(
        String betId,
        String userId,
        int eventId,
        int driverId,
        BigDecimal amount,
        BigDecimal odds,
        BigDecimal potentialWinnings,
        BetStatusDto status,
        LocalDateTime placedAt
) {
}
