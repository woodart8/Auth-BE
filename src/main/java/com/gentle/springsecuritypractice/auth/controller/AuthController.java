package com.gentle.springsecuritypractice.auth.controller;

import com.gentle.springsecuritypractice.auth.dto.LoginRequestDTO;
import com.gentle.springsecuritypractice.auth.dto.LoginResponseDTO;
import com.gentle.springsecuritypractice.auth.dto.SignUpRequestDTO;
import com.gentle.springsecuritypractice.auth.dto.TokenResponseDTO;
import com.gentle.springsecuritypractice.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 회원가입
    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDTO req) {
        Long userId = authService.signUp(req);
        return ResponseEntity.ok(userId);
    }

    // 로그인
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO req) {
        LoginResponseDTO loginResponse = authService.login(req);
        return ResponseEntity.ok(loginResponse);
    }

    // 카카오 로그인
    @PostMapping("/auth/login/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String accessCode) {
        LoginResponseDTO loginResponse = authService.kakaoLogin(accessCode);
        return ResponseEntity.ok(loginResponse);
    }

    // 로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 엑세스 토큰 갱신
    @PostMapping("/auth/reissue")
    public ResponseEntity<?> reissueToken(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponseDTO tokenResponse = authService.reissue(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

}
