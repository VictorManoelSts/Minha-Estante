package com.backend_frameworks.minha_estante.dto.request;

import com.backend_frameworks.minha_estante.enums.Genero;
import com.backend_frameworks.minha_estante.enums.StatusLeitura;
import com.backend_frameworks.minha_estante.enums.TipoLeitura;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LivroRequest(
        @NotBlank
        @Size(max = 256)
        String titulo,

        @NotBlank
        @Size(max = 256)
        String autor,

        @NotNull
        @Min(1)
        Integer paginas,

        @Min(0)
        Integer paginaAtual,

        @NotNull
        Genero genero,

        @NotNull
        TipoLeitura tipoLeitura,

        StatusLeitura statusLeitura
) {
}
