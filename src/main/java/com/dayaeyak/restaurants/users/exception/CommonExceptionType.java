package com.dayaeyak.restaurants.users.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonExceptionType implements ExceptionType {

    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 유저 ID입니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 유저 권한입니다."),
    INVALID_RESOURCE_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 리소스입니다."),
    INVALID_RESOLVER_CLASS(HttpStatus.BAD_REQUEST, "resolver 클래스는 OwnerResolver를 구현해야 함."),
    REQUEST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "요청 접근 권한이 부족합니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
