package com.dayaeyak.restaurants.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(BusinessException ex, HttpServletRequest req) {
        var code = ex.getErrorCode();  // var -> 컴파일러가 이 부분 지역변수 형식을 추론함, ex) ErrorCode code = ex.getErrorCode();
        var pd = ProblemDetail.forStatusAndDetail(code.getStatus(), ex.getMessage());
        pd.setTitle(code.name());
        pd.setProperty("code", code.name());
        pd.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(code.getStatus()).body(pd);
    }
}
