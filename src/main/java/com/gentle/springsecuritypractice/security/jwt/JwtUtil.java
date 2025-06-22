package com.gentle.springsecuritypractice.security.jwt;

import com.gentle.springsecuritypractice.common.exception.ErrorCode;
import com.gentle.springsecuritypractice.common.exception.CommonException;
import com.gentle.springsecuritypractice.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
public class JwtUtil {

    private final Key secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtUtil(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpirationTime = jwtProperties.getAccessExpirationTime();
        this.refreshExpirationTime = jwtProperties.getRefreshExpirationTime();
    }

    public String generateAccessToken(String subject, List<String> roles) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime * 1000L))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime * 1000L))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public JwtToken createAuthTokens(User user) {
        if (user.getUserId() != null) {
            String subject = String.valueOf(user.getUserId());
            List<String> roles = getRoles(user);
            String accessToken = generateAccessToken(subject, roles);
            String refreshToken = generateRefreshToken(subject);

            return new JwtToken(accessToken, refreshToken);
        } else throw new CommonException(ErrorCode.INVALID_PARAMETER);
    }

    public JwtToken reissueAccessToken(User user, String refreshToken) {
        String subject = getSubject(refreshToken);
        List<String> roles = getRoles(user);

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

    public List<String> getRoles(User user) {
        return Arrays.stream(user.getUserRole().split(","))
                .map(String::trim)
                .map(x -> "ROLE_" + x)
                .toList();
    }

    public void validateToken(String token) {
        JwtTokenValidator.validate(token, secretKey);
    }

}
