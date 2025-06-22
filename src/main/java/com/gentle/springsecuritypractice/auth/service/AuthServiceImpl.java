package com.gentle.springsecuritypractice.auth.service;

import com.gentle.springsecuritypractice.auth.dto.*;
import com.gentle.springsecuritypractice.auth.utility.KakaoUtil;
import com.gentle.springsecuritypractice.auth.utility.NaverUtil;
import com.gentle.springsecuritypractice.common.exception.ErrorCode;
import com.gentle.springsecuritypractice.common.exception.CommonException;
import com.gentle.springsecuritypractice.security.jwt.JwtProperties;
import com.gentle.springsecuritypractice.security.jwt.JwtToken;
import com.gentle.springsecuritypractice.security.jwt.JwtUtil;
import com.gentle.springsecuritypractice.redis.utility.RedisUtil;
import com.gentle.springsecuritypractice.user.aggregate.SignUpPath;
import com.gentle.springsecuritypractice.user.aggregate.UserRole;
import com.gentle.springsecuritypractice.user.aggregate.UserStatus;
import com.gentle.springsecuritypractice.user.entity.User;
import com.gentle.springsecuritypractice.user.repository.UserRepository;
import com.gentle.springsecuritypractice.auth.validator.LoginValidator;
import com.gentle.springsecuritypractice.auth.validator.SignUpValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;
    private final KakaoUtil kakaoUtil;
    private final NaverUtil naverUtil;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           JwtProperties jwtProperties,
                           RedisUtil redisUtil,
                           KakaoUtil kakaoUtil,
                           NaverUtil naverUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
        this.redisUtil = redisUtil;
        this.kakaoUtil = kakaoUtil;
        this.naverUtil = naverUtil;
    }

    @Override
    public Long signUp(SignUpRequestDTO req) {
        SignUpValidator.validate(req);

        User user = userRepository.findByEmailAndSignUpPath(req.getEmail(), SignUpPath.valueOf(req.getSignUpPath()))
                .orElse(null);

        if (user != null) throw new CommonException(ErrorCode.DUPLICATE_USER);

        return userRepository.save(User.builder()
                .userName(req.getUserName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .userStatus(UserStatus.ACTIVE)
                .signUpPath(SignUpPath.valueOf(req.getSignUpPath()))
                .createdAt(LocalDateTime.now().withNano(0))
                .userRole(UserRole.ENTERPRISE.name())
                .build()).getUserId();
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO req) {
        LoginValidator.validate(req);

        User user = userRepository.findByEmailAndSignUpPath(req.getEmail(), SignUpPath.valueOf(req.getSignUpPath()))
                .orElse(null);

        if (user == null) {
            throw new CommonException(ErrorCode.INVALID_EMAIL);
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_PASSWORD);
        }

        JwtToken jwtToken = jwtUtil.createAuthTokens(user);
        redisUtil.setValue(
                "refresh:" + user.getUserId(),
                jwtToken.getRefreshToken(),
                jwtProperties.getRefreshExpirationTime(),
                TimeUnit.SECONDS
        );

        return LoginResponseDTO.builder()
                .userId(user.getUserId())
                .token(jwtToken)
                .build();
    }

    @Override
    public LoginResponseDTO kakaoLogin(String accessCode) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        String email = kakaoProfile.getKakaoAccount().getEmail();

        User user = userRepository.findByEmailAndSignUpPath(email, SignUpPath.KAKAO)
                .orElseGet(() -> createNewUser(kakaoProfile));

        JwtToken jwtToken = jwtUtil.createAuthTokens(user);
        redisUtil.setValue(
                "refresh:" + user.getUserId(),
                jwtToken.getRefreshToken(),
                jwtProperties.getRefreshExpirationTime(),
                TimeUnit.SECONDS
        );

        return LoginResponseDTO.builder()
                .userId(user.getUserId())
                .token(jwtToken)
                .build();
    }

    @Override
    public LoginResponseDTO naverLogin(String accessCode) {
        NaverDTO.OAuthToken oAuthToken = naverUtil.requestToken(accessCode);
        NaverDTO.NaverProfile naverProfile = naverUtil.requestProfile(oAuthToken);
        String email = naverProfile.getEmail();

        User user = userRepository.findByEmailAndSignUpPath(email, SignUpPath.NAVER)
                .orElseGet(() -> createNewUser(naverProfile));

        JwtToken jwtToken = jwtUtil.createAuthTokens(user);
        redisUtil.setValue(
                "refresh:" + user.getUserId(),
                jwtToken.getRefreshToken(),
                jwtProperties.getRefreshExpirationTime(),
                TimeUnit.SECONDS
        );

        return LoginResponseDTO.builder()
                .userId(user.getUserId())
                .token(jwtToken)
                .build();
    }

    @Override
    public void logout(String accessToken) {
        jwtUtil.validateToken(accessToken);
        String userId = jwtUtil.getSubject(accessToken);
        redisUtil.deleteValue("refresh:" + userId);
    }

    @Override
    public TokenResponseDTO reissue(String refreshToken) {
        jwtUtil.validateToken(refreshToken);
        String subject = jwtUtil.getSubject(refreshToken);

        User user;
        try {
            user = userRepository.findById(Long.parseLong(subject))
                    .orElse(null);
        } catch (NumberFormatException e) {
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        }

        if (user == null) {
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        }

        String storedToken = redisUtil.getValue("refresh:" + user.getUserId());
        if (refreshToken.equals(storedToken)) {
            JwtToken jwtToken = jwtUtil.reissueAccessToken(user, refreshToken);
            return TokenResponseDTO.builder()
                    .token(jwtToken)
                    .build();
        } else {
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        }
    }

    private User createNewUser(KakaoDTO.KakaoProfile kakaoProfile) {
        User newUser = User.builder()
                .userName(kakaoProfile.getKakaoAccount().getProfile().getNickname())
                .email(kakaoProfile.getKakaoAccount().getEmail())
                .userStatus(UserStatus.ACTIVE)
                .signUpPath(SignUpPath.KAKAO)
                .createdAt(LocalDateTime.now().withNano(0))
                .userRole(UserRole.ENTERPRISE.name())
                .build();
        return userRepository.save(newUser);
    }

    private User createNewUser(NaverDTO.NaverProfile naverProfile) {
        User newUser = User.builder()
                .userName(naverProfile.getNickname())
                .email(naverProfile.getEmail())
                .userStatus(UserStatus.ACTIVE)
                .signUpPath(SignUpPath.NAVER)
                .createdAt(LocalDateTime.now().withNano(0))
                .userRole(UserRole.ENTERPRISE.name())
                .build();
        return userRepository.save(newUser);
    }

}
