package com.mateja.f1betting.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private BigDecimal balance;

    public static User createUserWithDefaultBalance(final String userId) {
        return User.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(100))
                .build();
    }
}
