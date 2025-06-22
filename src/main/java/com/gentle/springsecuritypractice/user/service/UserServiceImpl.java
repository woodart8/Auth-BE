package com.gentle.springsecuritypractice.user.service;

import com.gentle.springsecuritypractice.common.exception.ErrorCode;
import com.gentle.springsecuritypractice.common.exception.CommonException;
import com.gentle.springsecuritypractice.user.dto.UserResponseDTO;
import com.gentle.springsecuritypractice.user.entity.User;
import com.gentle.springsecuritypractice.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO loadUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
    }

}
