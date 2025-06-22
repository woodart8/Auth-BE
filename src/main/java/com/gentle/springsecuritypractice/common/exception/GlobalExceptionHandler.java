package com.gentle.springsecuritypractice.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException e) {
        return ResponseEntity.status(e.getCode()).body(new ErrorResponse(e));
    }

}
