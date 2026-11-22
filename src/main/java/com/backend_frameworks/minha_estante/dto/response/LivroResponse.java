package com.backend_frameworks.minha_estante.dto.response;

import com.backend_frameworks.minha_estante.enums.Genero;
import com.backend_frameworks.minha_estante.enums.StatusLeitura;
import com.backend_frameworks.minha_estante.enums.TipoLeitura;

import java.util.UUID;

public record LivroResponse(
        UUID id,
        String titulo,
        String autor,
        Integer paginas,
        Integer paginaAtual,
        Genero genero,
        TipoLeitura tipoLeitura,
        StatusLeitura statusLeitura
) {
}
