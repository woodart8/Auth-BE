package com.gentle.springsecuritypractice.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

public class KakaoDTO {

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OAuthToken {
        private String accessToken;
        private String tokenType;
        private String refreshToken;
        private int expiresIn;
        private String scope;
        private int refreshTokenExpiresIn;
    }

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KakaoProfile {
        private Long id;
        private String connectedAt;
        private Properties properties;
        private KakaoAccount kakaoAccount;

        @Getter
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Properties {
            private String nickname;
        }

        @Getter
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class KakaoAccount {
            private String email;
            private Boolean isEmailVerified;
            private Boolean hasEmail;
            private Boolean profileNicknameNeedsAgreement;
            private Boolean emailNeedsAgreement;
            private Boolean isEmailValid;
            private Profile profile;

            @Getter
            @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
            public static class Profile {
                private String nickname;
                private Boolean isDefaultNickname;
            }
        }
    }
}
