package com.mateja.f1betting.domain.model.outcome;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventOutcomeProcessingResult {
    int eventId;
    int winnerDriverId;
    BigDecimal totalPayout;
    long totalBetsUpdated;
}
