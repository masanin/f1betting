package com.mateja.f1betting.adapter.external.openf1.model;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class DriverFilter {
    Collection<Integer> sessionKeys;
    Integer driverId;
}
