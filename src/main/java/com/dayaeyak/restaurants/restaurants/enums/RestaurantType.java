package com.dayaeyak.restaurants.restaurants.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum RestaurantType {
    KOREAN("한식"),
    CHINESE("중식"),
    WESTERN("양식"),
    JAPANESE("일식"),
    HOF("호프"); // 호프집은 청소년 출입 금지

    @JsonValue
    private final String name;

    @JsonCreator
    private static RestaurantType of(String name) {
        return Stream.of(RestaurantType.values())
                .filter(restaurantType ->
                        restaurantType.name().equalsIgnoreCase(name) ||
                                restaurantType.name.equals(name))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException("음식점 타입명이 올바르지 않습니다."));
    }

}