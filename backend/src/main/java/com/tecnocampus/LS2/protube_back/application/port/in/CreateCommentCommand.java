package com.tecnocampus.LS2.protube_back.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentCommand(
        @NotBlank(message = "El comentario no puede estar vacío.")
        @Size(
                max = 2000,
                message = "El comentario admite hasta 2000 caracteres."
        )
        String content
) {
}
