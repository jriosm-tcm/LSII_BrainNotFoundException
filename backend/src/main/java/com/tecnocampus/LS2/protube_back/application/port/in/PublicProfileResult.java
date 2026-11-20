package com.tecnocampus.LS2.protube_back.application.port.in;

public record PublicProfileResult(
        String id,
        String username,
        String description,
        String avatarUrl
) {
}