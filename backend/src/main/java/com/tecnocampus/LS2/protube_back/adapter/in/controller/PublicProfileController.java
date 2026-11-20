package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.application.port.in.ProfileManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.in.PublicProfileResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class PublicProfileController {

    private final ProfileManagementUseCase profileUseCase;

    public PublicProfileController(ProfileManagementUseCase profileUseCase) {
        this.profileUseCase = profileUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicProfileResult> getPublicProfile(@PathVariable("id") String id) {
        return ResponseEntity.of(
                profileUseCase.getPublicProfile(id)
        );
    }
}
