package com.gentle.springsecuritypractice.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_TOKEN(400, "INVALID_TOKEN","유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "EXPIRED_TOKEN","만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(400, "UNSUPPORTED_TOKEN","지원하지 않는 토큰입니다."),
    MALFORMED_TOKEN(400, "MALFORMED_TOKEN","올바르지 않은 토큰입니다."),
    DUPLICATE_USER(400, "DUPLICATE_USER","이미 가입된 회원입니다."),
    INVALID_NAME(400, "INVALID_NAME","유효하지 않은 회원명입니다."),
    INVALID_PASSWORD(400, "INVALID_PASSWORD","유효하지 않은 비밀번호입니다."),
    INVALID_EMAIL(400, "INVALID_EMAIL","유효하지 않은 이메일입니다."),
    INVALID_SIGNUP_PATH(400, "INVALID_SIGNUP_PATH","유효하지 않은 회원가입 경로입니다."),
    INVALID_PARAMETER(400, "INVALID_PARAMETER","유효하지 않은 요청 인자입니다."),
    NOT_FOUND_USER(404, "NOT_FOUND_USER","해당 회원을 찾을 수 없습니다."),
    PARSE_JSON_FAILED(500, "PARSE_JSON_FAILED", "JSON 파싱 중 에러가 발생했습니다.");

    private final int code;
    private final String error;
    private final String message;

    ErrorCode(int code, String error, String message) {
        this.code = code;
        this.error = error;
        this.message = message;
    }

}
