package com.gentle.springsecuritypractice.common.security.jwt;

import com.gentle.springsecuritypractice.common.exception.CommonException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            log.info(token);
            try {
                jwtUtil.validateToken(token);
                Authentication authentication = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (CommonException ex) {
                response.setStatus(ex.getCode());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");  // 응답 인코딩을 UTF-8로 설정

                // JSON 응답 작성
                String jsonResponse = String.format(
                        "{\"code\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                        ex.getCode(),
                        ex.getError(),
                        ex.getMessage()
                );

                PrintWriter writer = response.getWriter();
                writer.write(jsonResponse);
                writer.flush();
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

