package com.gentle.springsecuritypractice.user.controller;

import com.gentle.springsecuritypractice.user.dto.*;
import com.gentle.springsecuritypractice.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(name = "id") Long id) {
        UserResponseDTO userResponse = userService.loadUserById(id);
        return ResponseEntity.ok(userResponse);
    }

}
