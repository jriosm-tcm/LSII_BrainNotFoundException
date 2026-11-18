package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.services.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record Registration(@NotBlank @Email String email,
                               @NotBlank @Size(min = 8, max = 72) String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Registration input) {
        return authService.register(input.email(), input.password())
                .<ResponseEntity<?>>map(email ->
                        ResponseEntity.status(HttpStatus.CREATED).body(Map.of("email", email)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Email already registered")));
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("email", authentication.getName());
    }
}
