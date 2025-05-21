package com.gentle.springsecuritypractice.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gentle.springsecuritypractice.common.security.jwt.JwtToken;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {
    @JsonProperty("user_id")
    private Long userId;
    private JwtToken token;
}
