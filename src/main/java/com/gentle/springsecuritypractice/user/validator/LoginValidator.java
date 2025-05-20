package com.gentle.springsecuritypractice.user.validator;

import com.gentle.springsecuritypractice.common.aggregate.ErrorCode;
import com.gentle.springsecuritypractice.common.exception.CommonException;
import com.gentle.springsecuritypractice.user.aggregate.SignUpPath;
import com.gentle.springsecuritypractice.user.dto.LoginRequestDTO;

public class LoginValidator {

    public static void validate(LoginRequestDTO req) {
        String email = req.getEmail();
        if (email == null || email.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_EMAIL);
        }

        String password = req.getPassword();
        if (password == null || password.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_PASSWORD);
        }

        String signUpPath = req.getSignUpPath();
        if (!SignUpPath.isValid(signUpPath)) {
            throw new CommonException(ErrorCode.INVALID_SIGNUP_PATH);
        }
    }

}
