package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.users.AppUser;
import com.tecnocampus.LS2.protube_back.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserRepository users;
    public ProfileController(UserRepository users) { this.users = users; }
    public record ProfileUpdate(@Size(max = 500) String description, @Size(max = 500) String avatarUrl) {}
    @GetMapping("/me")
    public ResponseEntity<?> mine(Authentication authentication) {
        return users.findByEmailIgnoreCase(authentication.getName())
                .map(user -> ResponseEntity.ok(Map.of("email", user.getEmail(),
                        "description", user.getDescription(), "avatarUrl", user.getAvatarUrl())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PutMapping("/me")
    public ResponseEntity<?> update(@Valid @RequestBody ProfileUpdate update, Authentication authentication) {
        return users.findByEmailIgnoreCase(authentication.getName()).map(user -> {
            user.setDescription(update.description() == null ? "" : update.description());
            user.setAvatarUrl(update.avatarUrl() == null ? "" : update.avatarUrl());
            users.save(user);
            return ResponseEntity.ok(Map.of("email", user.getEmail(), "description", user.getDescription(),
                    "avatarUrl", user.getAvatarUrl()));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/{email}")
    public ResponseEntity<?> publicProfile(@PathVariable String email) {
        return users.findByEmailIgnoreCase(email).map(user ->
                ResponseEntity.ok(Map.of("email", user.getEmail(), "description", user.getDescription(),
                        "avatarUrl", user.getAvatarUrl()))).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
