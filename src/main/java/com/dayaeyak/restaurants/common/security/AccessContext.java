package com.dayaeyak.restaurants.common.security;

import com.dayaeyak.restaurants.users.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccessContext {
    private final Long userId;
    private final UserRole role;

    public static AccessContext of(Long userId, UserRole role) {
        return new AccessContext(userId, role);
    }

}
