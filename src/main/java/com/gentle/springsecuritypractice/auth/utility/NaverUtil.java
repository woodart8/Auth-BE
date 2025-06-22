package com.gentle.springsecuritypractice.auth.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gentle.springsecuritypractice.auth.dto.NaverDTO;
import com.gentle.springsecuritypractice.common.exception.ErrorCode;
import com.gentle.springsecuritypractice.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class NaverUtil {

    @Value("${naver.auth.client}")
    private String client;
    @Value("${naver.auth.secret}")
    private String secret;

    public NaverDTO.OAuthToken requestToken(String accessCode) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client);
        params.add("client_secret", secret);
        params.add("code", accessCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
          "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                request,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        NaverDTO.OAuthToken oAuthToken;
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), NaverDTO.OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new CommonException(ErrorCode.PARSE_JSON_FAILED);
        }

        return oAuthToken;
    }

    public NaverDTO.NaverProfile requestProfile(NaverDTO.OAuthToken oAuthToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization","Bearer "+ oAuthToken.getAccessToken());

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                request,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        NaverDTO naverDTO;
        try {
            naverDTO = objectMapper.readValue(response.getBody(), NaverDTO.class);
        } catch (JsonProcessingException e) {
            throw new CommonException(ErrorCode.PARSE_JSON_FAILED);
        }

        return naverDTO.getResponse();
    }
}
