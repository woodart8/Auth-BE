package com.gentle.springsecuritypractice.user.service;

import com.gentle.springsecuritypractice.common.aggregate.ErrorCode;
import com.gentle.springsecuritypractice.common.exception.CommonException;
import com.gentle.springsecuritypractice.common.security.jwt.JwtToken;
import com.gentle.springsecuritypractice.common.security.jwt.JwtTokenProvider;
import com.gentle.springsecuritypractice.user.aggregate.SignUpPath;
import com.gentle.springsecuritypractice.user.aggregate.UserRole;
import com.gentle.springsecuritypractice.user.aggregate.UserStatus;
import com.gentle.springsecuritypractice.user.dto.LoginRequestDTO;
import com.gentle.springsecuritypractice.user.dto.LoginResponseDTO;
import com.gentle.springsecuritypractice.user.dto.SignUpRequestDTO;
import com.gentle.springsecuritypractice.user.entity.User;
import com.gentle.springsecuritypractice.user.repository.UserRepository;
import com.gentle.springsecuritypractice.user.validator.LoginValidator;
import com.gentle.springsecuritypractice.user.validator.SignUpValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
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

        JwtToken jwtToken = createAuthTokens(user);

        return LoginResponseDTO.builder()
                .userId(user.getUserId())
                .token(jwtToken)
                .build();
    }

    private JwtToken createAuthTokens(User user) {
        if (user.getUserId() != null) {
            String subject = String.valueOf(user.getUserId());
            List<String> roles = Arrays
                    .stream(user.getUserRole().split(","))
                    .map(String::trim)
                    .map(x -> "ROLE_" + x).toList();
            String accessToken = jwtTokenProvider.generateAccessToken(subject, roles);
            String refreshToken = jwtTokenProvider.generateRefreshToken(subject, roles);

            return new JwtToken(accessToken, refreshToken);
        } else throw new CommonException(ErrorCode.INVALID_PARAMETER);
    }

}
