package com.gentle.springsecuritypractice.user.service;

import com.gentle.springsecuritypractice.user.dto.LoginRequestDTO;
import com.gentle.springsecuritypractice.user.dto.LoginResponseDTO;
import com.gentle.springsecuritypractice.user.dto.SignUpRequestDTO;

public interface AuthService {

    Long signUp(SignUpRequestDTO req);

    LoginResponseDTO login(LoginRequestDTO req);

}
