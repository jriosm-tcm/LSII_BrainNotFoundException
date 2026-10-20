package com.tecnocampus.LS2.protube_back.dtos;

public record VideoDTO(
        Long id,
        String title,
        String description,
        String url,
        String thumbnailUrl
) {}