package com.backend_frameworks.minha_estante.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        String telefone,
        LocalDate dataNascimento
) {
}
