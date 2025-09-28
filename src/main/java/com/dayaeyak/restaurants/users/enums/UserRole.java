package com.dayaeyak.restaurants.users.enums;

import com.dayaeyak.restaurants.users.exception.CommonExceptionType;
import com.dayaeyak.restaurants.users.exception.CustomRuntimeException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum UserRole {

    MASTER,
    SELLER,
    NORMAL,
    ;

    @JsonCreator
    public static UserRole of(String role) {
        return Stream.of(UserRole.values())
                .filter(userRole -> userRole.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeException(CommonExceptionType.INVALID_USER_ROLE));
    }
}
