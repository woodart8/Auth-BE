package com.gentle.springsecuritypractice.user.service;

import com.gentle.springsecuritypractice.user.dto.LoginRequestDTO;
import com.gentle.springsecuritypractice.user.dto.LoginResponseDTO;
import com.gentle.springsecuritypractice.user.dto.SignUpRequestDTO;
import com.gentle.springsecuritypractice.user.dto.TokenResponseDTO;
import com.gentle.springsecuritypractice.user.entity.User;

public interface AuthService {

    Long signUp(SignUpRequestDTO req);

    LoginResponseDTO login(LoginRequestDTO req);

    TokenResponseDTO reissue(String refreshToken);

}
