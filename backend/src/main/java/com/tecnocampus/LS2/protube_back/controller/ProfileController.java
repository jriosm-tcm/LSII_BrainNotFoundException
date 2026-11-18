package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.services.ProfileService;
import com.tecnocampus.LS2.protube_back.services.AvatarService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profiles;
    private final AvatarService avatars;

    public ProfileController(ProfileService profiles, AvatarService avatars) {
        this.profiles = profiles;
        this.avatars = avatars;
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

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(@RequestPart("file") MultipartFile file, Authentication authentication) throws IOException {
        try {
            return ResponseEntity.ok(java.util.Map.of("avatarUrl", avatars.upload(authentication.getName(), file)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/avatar/{name}")
    public ResponseEntity<Resource> avatar(@PathVariable String name) throws IOException {
        try {
            return ResponseEntity.ok().contentType(avatars.mediaType(name))
                    .header("Cache-Control", "public, max-age=3600").body(avatars.read(name));
        } catch (java.io.FileNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> publicProfile(@PathVariable String email) {
        return profiles.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
