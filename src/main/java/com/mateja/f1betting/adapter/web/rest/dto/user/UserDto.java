package com.mateja.f1betting.adapter.web.rest.dto.user;

import java.math.BigDecimal;

public record UserDto(String userId, BigDecimal balance) {
}
