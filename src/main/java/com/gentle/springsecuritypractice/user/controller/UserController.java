package com.gentle.springsecuritypractice.user.controller;

import com.gentle.springsecuritypractice.user.dto.*;
import com.gentle.springsecuritypractice.user.service.AuthService;
import com.gentle.springsecuritypractice.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // 회원가입
    @PostMapping("signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDTO req) {
        Long userId = authService.signUp(req);
        return ResponseEntity.ok(userId);
    }

    // 로그인
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO req) {
        LoginResponseDTO loginResponse = authService.login(req);
        return ResponseEntity.ok(loginResponse);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 엑세스 토큰 갱신
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponseDTO tokenResponse = authService.reissue(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(name = "id") Long id) {
        UserResponseDTO userResponse = userService.loadUserById(id);
        return ResponseEntity.ok(userResponse);
    }

}
