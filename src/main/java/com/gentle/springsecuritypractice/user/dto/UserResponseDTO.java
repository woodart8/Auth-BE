package com.gentle.springsecuritypractice.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDTO {

    @JsonProperty("user_id")
    Long userId;

    @JsonProperty("user_name")
    String userName;

    String email;

}
