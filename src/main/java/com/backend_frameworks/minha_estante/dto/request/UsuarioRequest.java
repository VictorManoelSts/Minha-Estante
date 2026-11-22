package com.backend_frameworks.minha_estante.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UsuarioRequest(
        @NotBlank
        @Size(max = 256)
        String nome,

        @NotBlank
        @Email
        @Size(max = 256)
        String email,

        @NotBlank
        @Pattern(regexp = "\\d{11}")
        String telefone,

        @NotNull
        @Past
        LocalDate dataNascimento,

        @NotBlank
        @Size(min = 8, max = 72)
        String senha

) {
}
