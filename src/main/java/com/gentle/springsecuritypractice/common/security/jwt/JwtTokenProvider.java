package com.gentle.springsecuritypractice.common.security.jwt;

import com.gentle.springsecuritypractice.common.aggregate.ErrorCode;
import com.gentle.springsecuritypractice.common.exception.CommonException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtTokenProvider(
            @Value("${token.secret}") String secretKey,
            @Value("${token.access-expiration-time}") long accessExpirationTime,
            @Value("${token.refresh-expiration-time}") long refreshExpirationTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    public String generateAccessToken(String subject, List<String> roles) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String subject, List<String> roles) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public JwtToken refreshAccessToken(String refreshToken) {
        JwtTokenValidator.validate(refreshToken, secretKey);

        String subject = getSubject(refreshToken);
        List<String> roles = getRoles(refreshToken);

        String newAccessToken = generateAccessToken(subject, roles);

        return new JwtToken(newAccessToken, refreshToken);
    }

    public Authentication getAuthentication(String token) {
        String subject = getSubject(token);
        List<String> roles = getRoles(token);
        if (roles == null || roles.isEmpty()) {
            throw new CommonException(ErrorCode.MALFORMED_TOKEN);
        }
        Collection<? extends GrantedAuthority> authorities = roles
                .stream()
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(subject, "", authorities);
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = parseClaims(token);
        Object rolesObject = claims.get("roles");
        List<String> roles = new ArrayList<>();

        if (rolesObject instanceof String) {
            roles.add((String) rolesObject);
        } else if (rolesObject instanceof List<?>) {
            for (Object role : (List<?>) rolesObject) {
                roles.add(role.toString());
            }
        } else {
            throw new CommonException(ErrorCode.MALFORMED_TOKEN);
        }

        return roles;
    }

}
