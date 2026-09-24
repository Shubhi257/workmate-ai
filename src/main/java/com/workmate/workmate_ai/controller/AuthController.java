package com.workmate.workmate_ai.controller;

import com.workmate.workmate_ai.dto.LoginRequest;
import com.workmate.workmate_ai.dto.LoginResponse;
import com.workmate.workmate_ai.dto.RegisterRequest;
import com.workmate.workmate_ai.dto.RegisterResponse;
import com.workmate.workmate_ai.entity.User;
import com.workmate.workmate_ai.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.workmate.workmate_ai.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = userService.createUser(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
    }
}