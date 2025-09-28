package com.dayaeyak.restaurants.kafka.producer.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum BusinessType {
    PERFORMANCE("공연"),
    EXHIBITION("전시회"),
    RESTAURANT("음식점"),
    ;
    @JsonValue
    private final String type;

    @JsonCreator
    public static BusinessType of(String type) {
        return Stream.of(BusinessType.values())
                .filter(businessType -> businessType.type.equalsIgnoreCase(type)
                        || businessType.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}

