package com.tecnocampus.LS2.protube_back.adapter.in.dto;

public record VideoDTO(
        Long id,
        String title,
        String description,
        String url,
        String thumbnailUrl
) {}
