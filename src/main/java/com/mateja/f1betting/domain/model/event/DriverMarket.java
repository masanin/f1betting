package com.mateja.f1betting.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverMarket {
    private int driverId;
    private String fullName;
    private BigDecimal odds;
}
