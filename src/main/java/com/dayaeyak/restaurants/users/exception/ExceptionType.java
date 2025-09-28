package com.dayaeyak.restaurants.users.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionType {

    HttpStatus getStatus();
    String getMessage();
}

