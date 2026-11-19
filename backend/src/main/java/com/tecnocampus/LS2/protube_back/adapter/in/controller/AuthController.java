package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/auth")
@SecurityRequirement(name = "basicAuth")
public class AuthController {

    public record AuthenticatedUserResponse(String email) {
    }

    @GetMapping("/me")
    public AuthenticatedUserResponse me(Authentication authentication) {
        return new AuthenticatedUserResponse(
                authentication.getName()
        );
    }

}
