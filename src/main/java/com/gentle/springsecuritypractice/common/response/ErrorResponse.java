package com.gentle.springsecuritypractice.common.response;

import com.gentle.springsecuritypractice.common.exception.CommonException;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int code;
    private final String error;
    private final String message;

    public ErrorResponse(CommonException ex) {
        this.code = ex.getCode();
        this.error = ex.getError();
        this.message = ex.getMessage();
    }

}
