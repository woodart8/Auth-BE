package com.gentle.springsecuritypractice.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NaverDTO {

    private String resultcode;
    private String message;
    private NaverProfile response;

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OAuthToken {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Integer expiresIn;
    }

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class NaverProfile {
        String id;
        String nickname;
        String name;
        String email;
        String gender;
        String age;
        String birthday;
        String profileImage;
        String birthyear;
        String mobile;
    }

}
