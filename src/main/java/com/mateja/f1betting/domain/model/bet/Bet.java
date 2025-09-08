package com.mateja.f1betting.domain.model.bet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bet {
    private String betId;
    private String userId;
    private int eventId;
    private int driverId;
    private BigDecimal amount;
    private BigDecimal odds;
    private BetStatus status;
    private BigDecimal potentialWinnings;
    private LocalDateTime placedAt;

    public static Bet createBet(
            final String userId,
            final int eventId,
            final int driverId,
            final BigDecimal amount,
            final BigDecimal odds
    ) {
        final BigDecimal potentialWinnings = amount.multiply(odds);

        return Bet.builder()
                .userId(userId)
                .eventId(eventId)
                .driverId(driverId)
                .amount(amount)
                .odds(odds)
                .status(BetStatus.PENDING)
                .potentialWinnings(potentialWinnings)
                .placedAt(LocalDateTime.now())
                .build();
    }
}
