package com.dayaeyak.restaurants.users.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomRuntimeException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public CustomRuntimeException(ExceptionType e) {
        super(e.getMessage());

        this.status = e.getStatus();
        this.message = e.getMessage();
    }
}
