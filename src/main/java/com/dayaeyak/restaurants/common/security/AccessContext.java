package com.dayaeyak.restaurants.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccessContext {
    private final Long userId;
    private final Role role;


    public static AccessContext of(Long userId, Role role) {
        return new AccessContext(userId, role);
    }

}
