package com.gentle.springsecuritypractice.auth.service;

import com.gentle.springsecuritypractice.auth.dto.LoginRequestDTO;
import com.gentle.springsecuritypractice.auth.dto.LoginResponseDTO;
import com.gentle.springsecuritypractice.auth.dto.SignUpRequestDTO;
import com.gentle.springsecuritypractice.auth.dto.TokenResponseDTO;

public interface AuthService {

    Long signUp(SignUpRequestDTO req);

    LoginResponseDTO login(LoginRequestDTO req);

    LoginResponseDTO kakaoLogin(String accessCode);

    LoginResponseDTO naverLogin(String accessCode);

    void logout(String token);

    TokenResponseDTO reissue(String refreshToken);

}
