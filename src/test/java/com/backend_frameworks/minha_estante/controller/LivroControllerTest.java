
package com.backend_frameworks.minha_estante.controller;

import com.backend_frameworks.minha_estante.dto.request.LivroRequest;
import com.backend_frameworks.minha_estante.dto.response.LivroResponse;
import com.backend_frameworks.minha_estante.enums.*;
import com.backend_frameworks.minha_estante.service.LivroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LivroControllerTest {

    @Mock
    private LivroService livroService;

    @InjectMocks
    private LivroController livroController;

    private MockMvc mockMvc;

    private final UUID livroId = UUID.randomUUID();

    private final Genero genero = Genero.values()[0];
    private final TipoLeitura tipo = TipoLeitura.values()[0];

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(livroController)
                .build();
    }

    private String criarJson() {
        return """
                {
                  "titulo": "O Hobbit",
                  "paginas": 310,
                  "paginaAtual": 150,
                  "genero": "%s",
                  "tipoLeitura": "%s",
                  "statusLeitura": "LENDO"
                }
                """.formatted(genero, tipo);
    }

    private LivroResponse criarResponse() {
        return new LivroResponse(
                livroId, "O Hobbit", 310, 150,
                genero, tipo, StatusLeitura.LENDO
        );
    }

    @Test
    void deveCadastrarLivro() throws Exception {

        // Arrange
        when(livroService.cadastrar(any(LivroRequest.class)))
                .thenReturn(criarResponse());

        // Act e Assert
        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("O Hobbit"))
                .andExpect(jsonPath("$.paginas").value(310));

        verify(livroService).cadastrar(any(LivroRequest.class));
    }

    @Test
    void deveListarLivros() throws Exception {

        // Arrange
        when(livroService.listar())
                .thenReturn(List.of(criarResponse()));

        // Act e Assert
        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("O Hobbit"));

        verify(livroService).listar();
    }

    @Test
    void deveBuscarLivroPorId() throws Exception {

        // Arrange
        when(livroService.buscarPorId(livroId))
                .thenReturn(criarResponse());

        // Act e Assert
        mockMvc.perform(get("/livros/{id}", livroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(livroId.toString()))
                .andExpect(jsonPath("$.titulo").value("O Hobbit"));

        verify(livroService).buscarPorId(livroId);
    }

    @Test
    void deveAtualizarLivro() throws Exception {

        // Arrange
        when(livroService.atualizar(
                eq(livroId), any(LivroRequest.class)))
                .thenReturn(criarResponse());

        // Act e Assert
        mockMvc.perform(put("/livros/{id}", livroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("O Hobbit"));

        verify(livroService).atualizar(
                eq(livroId), any(LivroRequest.class));
    }

    @Test
    void deveExcluirLivro() throws Exception {

        // Act e Assert
        mockMvc.perform(delete("/livros/{id}", livroId))
                .andExpect(status().isNoContent());

        verify(livroService).excluir(livroId);
    }
}
