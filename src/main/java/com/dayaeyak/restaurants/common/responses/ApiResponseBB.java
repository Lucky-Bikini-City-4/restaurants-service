package com.dayaeyak.restaurants.common.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ApiResponseBB<T>(
        String message,
        T data
) {

    public static <T> ResponseEntity<ApiResponseBB<T>> success(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status)
                .body(new ApiResponseBB<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseBB<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiResponseBB<>(message, null));
    }
}
