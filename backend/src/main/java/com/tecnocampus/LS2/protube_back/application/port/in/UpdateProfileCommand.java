package com.tecnocampus.LS2.protube_back.application.port.in;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProfileCommand(
        @NotNull(message = "Debes enviar la descripción, aunque esté vacía.")
        @Size(max = 500, message = "La descripción admite hasta 500 caracteres.")
        String description
) {
}
