package com.gentle.springsecuritypractice.user.service;

import com.gentle.springsecuritypractice.user.dto.LoginRequestDTO;
import com.gentle.springsecuritypractice.user.dto.LoginResponseDTO;
import com.gentle.springsecuritypractice.user.dto.SignUpRequestDTO;
import com.gentle.springsecuritypractice.user.dto.TokenResponseDTO;

public interface AuthService {

    Long signUp(SignUpRequestDTO req);

    LoginResponseDTO login(LoginRequestDTO req);

    void logout(String token);

    TokenResponseDTO reissue(String refreshToken);

}
