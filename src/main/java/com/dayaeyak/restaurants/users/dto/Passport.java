package com.dayaeyak.restaurants.users.dto;

import com.dayaeyak.restaurants.users.enums.UserRole;

public record Passport(
        Long userId,
        UserRole role
) {
}

