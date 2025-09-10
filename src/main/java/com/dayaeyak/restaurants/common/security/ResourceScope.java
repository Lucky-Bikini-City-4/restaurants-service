package com.dayaeyak.restaurants.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResourceScope {
    private final Long sellerId;

    public static ResourceScope of(Long sellerId) {
        return new ResourceScope(sellerId);
    }
}
