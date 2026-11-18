package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.services.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profiles;

    public ProfileController(ProfileService profiles) {
        this.profiles = profiles;
    }

    public record ProfileUpdate(@Size(max = 500) String description,
                                @Size(max = 500) String avatarUrl) {}

    @GetMapping("/me")
    public ResponseEntity<?> mine(Authentication authentication) {
        return profiles.findByEmail(authentication.getName())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<?> update(@Valid @RequestBody ProfileUpdate input, Authentication authentication) {
        return profiles.update(authentication.getName(), input.description(), input.avatarUrl())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> publicProfile(@PathVariable String email) {
        return profiles.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
