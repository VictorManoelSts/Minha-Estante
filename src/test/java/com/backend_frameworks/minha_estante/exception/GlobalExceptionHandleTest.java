
package com.backend_frameworks.minha_estante.exception;

import com.backend_frameworks.minha_estante.controller.LivroController;
import com.backend_frameworks.minha_estante.service.LivroService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private LivroService livroService;

    private MockMvc mockMvc;

    private final UUID livroId = UUID.randomUUID();

    @BeforeEach
    void configurar() {
        LivroController controller = new LivroController(livroService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveRetornar404QuandoLivroNaoExiste() throws Exception {

        // Arrange
        when(livroService.buscarPorId(livroId))
                .thenThrow(new EntityNotFoundException(
                        "Livro não encontrado"));

        // Act e Assert
        mockMvc.perform(get("/livros/{id}", livroId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Livro não encontrado"));

        verify(livroService).buscarPorId(livroId);
    }

    @Test
    void deveRetornar400QuandoRegraDeNegocioForInvalida()
            throws Exception {

        // Arrange
        doThrow(new IllegalArgumentException(
                "Página atual inválida"))
                .when(livroService).excluir(livroId);

        // Act e Assert
        mockMvc.perform(delete("/livros/{id}", livroId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro")
                        .value("Página atual inválida"));

        verify(livroService).excluir(livroId);
    }
}
