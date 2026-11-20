package com.tecnocampus.LS2.protube_back.application.port.in;

public record ProfileResult(
        String id,
        String username,
        String email,
        String description,
        String avatarUrl
) {
}
