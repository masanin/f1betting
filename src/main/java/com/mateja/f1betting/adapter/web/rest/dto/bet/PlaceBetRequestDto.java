package com.mateja.f1betting.adapter.web.rest.dto.bet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PlaceBetRequestDto(
        @NotBlank(message = "User ID is required")
        String userId,

        @NotNull(message = "Event ID is required")
        Integer eventId,

        @NotNull(message = "Driver ID is required")
        Integer driverId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Minimum bet amount is 0.01 EUR")
        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {
}
