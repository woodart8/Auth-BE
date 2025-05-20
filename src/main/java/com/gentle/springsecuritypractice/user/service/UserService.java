package com.gentle.springsecuritypractice.user.service;

import com.gentle.springsecuritypractice.user.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO loadUserById(long id);

}
