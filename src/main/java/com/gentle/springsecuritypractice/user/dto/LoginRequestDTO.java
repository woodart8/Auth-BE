package com.gentle.springsecuritypractice.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    private String email;

    private String password;

    @JsonProperty("signup_path")
    private String signUpPath;

}
