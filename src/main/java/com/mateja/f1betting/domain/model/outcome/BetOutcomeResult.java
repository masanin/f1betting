package com.mateja.f1betting.domain.model.outcome;

import java.math.BigDecimal;
import java.util.Map;

public record BetOutcomeResult(long totalBetsUpdated, Map<String, BigDecimal> userWinnings) {
}
