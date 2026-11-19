package com.tecnocampus.LS2.protube_back.application.port.in;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.charset.StandardCharsets;

public record RegisterUserCommand(
        @NotBlank(message = "Escribe un nombre de usuario.")
        @Size(max = 100, message = "El nombre admite hasta 100 caracteres.")
        String username,

        @NotBlank(message = "Escribe un correo.")
        @Email(message = "Introduce un correo válido.")
        @Size(max = 254, message = "El correo admite hasta 254 caracteres.")
        String email,

        @NotBlank(message = "Escribe una contraseña.")
        @Size(min = 15, message = "La contraseña debe tener al menos 15 caracteres.")
        String password
) {
    @JsonIgnore
    @AssertTrue(message = "La contraseña supera el máximo de 72 bytes.")
    public boolean isPasswordWithinBcryptLimit() {
        return password == null
                || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}
