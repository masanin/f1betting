package com.mateja.f1betting.adapter.external.openf1.mapper;

import com.mateja.f1betting.adapter.external.openf1.dto.OpenF1DriverResponse;
import com.mateja.f1betting.domain.model.event.DriverMarket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class F1DriverMarketMapper {
    private static final Random random = new Random();

    public static List<DriverMarket> toF1DriverMarkets(final Collection<OpenF1DriverResponse> driverMarkets) {
        return driverMarkets.stream()
                .map(F1DriverMarketMapper::toF1DriverMarket)
                .toList();
    }

    public static DriverMarket toF1DriverMarket(final OpenF1DriverResponse driver) {
        return DriverMarket.builder()
                .driverId(driver.getDriverNumber())
                .fullName(driver.getFullName())
                .odds(BigDecimal.valueOf(random.nextInt(2, 5)))
                .build();
    }
}
