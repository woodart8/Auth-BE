package com.gentle.springsecuritypractice.auth.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gentle.springsecuritypractice.auth.dto.KakaoDTO;
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
public class KakaoUtil {

    @Value("${kakao.auth.client}")
    private String client;
    @Value("${kakao.auth.redirect}")
    private String redirect;

    public KakaoDTO.OAuthToken requestToken(String accessCode) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client);
        params.add("redirect_url", redirect);
        params.add("code", accessCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                request,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        KakaoDTO.OAuthToken oAuthToken;
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), KakaoDTO.OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new CommonException(ErrorCode.PARSE_JSON_FAILED);
        }

        return oAuthToken;
    }

    public KakaoDTO.KakaoProfile requestProfile(KakaoDTO.OAuthToken oAuthToken){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization","Bearer "+ oAuthToken.getAccessToken());

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        KakaoDTO.KakaoProfile kakaoProfile;
        try {
            kakaoProfile = objectMapper.readValue(response.getBody(), KakaoDTO.KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new CommonException(ErrorCode.PARSE_JSON_FAILED);
        }

        return kakaoProfile;
    }

}
