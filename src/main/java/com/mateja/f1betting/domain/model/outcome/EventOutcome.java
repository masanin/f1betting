package com.mateja.f1betting.domain.model.outcome;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventOutcome {
    private int eventId;
    private int winnerDriverId;
}
