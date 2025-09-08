package com.mateja.f1betting.adapter.persistence.mapper;

import com.mateja.f1betting.adapter.persistence.entity.UserEntity;
import com.mateja.f1betting.domain.model.user.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntityMapper {
    public static UserEntity toEntity(final User user) {
        return UserEntity.builder()
                .userId(user.getUserId())
                .balance(user.getBalance())
                .build();
    }

    public static User toDomain(final UserEntity entity) {
        return User.builder()
                .userId(entity.getUserId())
                .balance(entity.getBalance())
                .build();
    }
}
