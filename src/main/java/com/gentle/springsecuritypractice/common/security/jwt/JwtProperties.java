package com.gentle.springsecuritypractice.common.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "token")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long accessExpirationTime;
    private long refreshExpirationTime;
}
