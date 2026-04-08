package org.example.gorevyonetimsistemi.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.model.LoginRequest;
import org.example.gorevyonetimsistemi.model.RegisterRequest;
import org.example.gorevyonetimsistemi.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam String token, jakarta.servlet.http.HttpServletResponse response) {
        return authService.verifyAccount(token, response);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return authService.login(loginRequest, response);
    }
}
