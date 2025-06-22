package com.gentle.springsecuritypractice.common.exception;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final ErrorCode errorCode;

    public CommonException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 부모 클래스 메시지도 설정
        this.errorCode = errorCode;
    }

    public int getCode() {
        return errorCode.getCode();
    }

    public String getError() {
        return errorCode.getError();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

}
