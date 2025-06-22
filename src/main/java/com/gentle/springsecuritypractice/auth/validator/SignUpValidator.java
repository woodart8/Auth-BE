package com.gentle.springsecuritypractice.auth.validator;

import com.gentle.springsecuritypractice.common.exception.ErrorCode;
import com.gentle.springsecuritypractice.common.exception.CommonException;
import com.gentle.springsecuritypractice.user.aggregate.SignUpPath;
import com.gentle.springsecuritypractice.auth.dto.SignUpRequestDTO;

public class SignUpValidator {

    public static void validate(SignUpRequestDTO req) {
        String userName = req.getUserName();
        if (userName == null || userName.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_NAME);
        }

        String email = req.getEmail();
        if (email == null || email.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_EMAIL);
        }

        String password = req.getPassword();
        if (password == null || password.length() < 8) {
            throw new CommonException(ErrorCode.INVALID_PASSWORD);
        }

        String signUpPath = req.getSignUpPath();
        if (!SignUpPath.isValid(signUpPath)) {
            throw new CommonException(ErrorCode.INVALID_SIGNUP_PATH);
        }
    }

}
