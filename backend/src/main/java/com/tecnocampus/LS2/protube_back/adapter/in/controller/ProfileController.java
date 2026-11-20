package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.application.port.in.ProfileManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.in.ProfileResult;
import com.tecnocampus.LS2.protube_back.application.port.in.UpdateProfileCommand;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile")
@SecurityRequirement(name = "basicAuth")
public class ProfileController {

    private final ProfileManagementUseCase profileUseCase;

    public ProfileController(ProfileManagementUseCase profileUseCase) {
        this.profileUseCase = profileUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResult> getMyProfile(
            Authentication authentication
    ) {
        return ResponseEntity.of(
                profileUseCase.getMyProfile(authentication.getName())
        );
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResult> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileCommand command
    ) {
        return ResponseEntity.of(
                profileUseCase.updateMyProfile(
                        authentication.getName(),
                        command
                )
        );
    }

    @PostMapping(
            value = "/me/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProfileResult> uploadAvatar(@RequestPart("file") MultipartFile file, Authentication authentication
    ) throws IOException {
        if (file.isEmpty()
                || file.getSize() > ProfileManagementUseCase.MAX_AVATAR_BYTES) {
            throw new IllegalArgumentException(
                    "Selecciona una imagen de hasta 2 MB."
            );
        }
        return ResponseEntity.of(
                profileUseCase.uploadMyAvatar(
                        authentication.getName(),
                        file.getBytes()
                )
        );
    }
}
