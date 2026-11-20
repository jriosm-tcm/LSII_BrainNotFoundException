package com.tecnocampus.LS2.protube_back.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateVideoMetadataCommand(
        @NotBlank(message = "El título es obligatorio.")
        @Size(
                max = 200,
                message = "El título admite hasta 200 caracteres."
        )
        String title,

        @Size(
                max = 5000,
                message = "La descripción admite hasta 5000 caracteres."
        )
        String description
) {
}