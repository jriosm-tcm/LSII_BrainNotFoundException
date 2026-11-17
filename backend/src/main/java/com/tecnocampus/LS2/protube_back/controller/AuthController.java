package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.users.AppUser;
import com.tecnocampus.LS2.protube_back.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }
    public record Registration(@NotBlank @Email String email, @NotBlank @Size(min = 8, max = 72) String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Registration input) {
        String email = input.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(email)) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered"));
        try {
            users.saveAndFlush(new AppUser(email, encoder.encode(input.password())));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("email", email));
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("email", authentication.getName());
    }
}
