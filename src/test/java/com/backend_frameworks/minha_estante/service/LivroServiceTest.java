
package com.backend_frameworks.minha_estante.service;

import com.backend_frameworks.minha_estante.dto.request.LivroRequest;
import com.backend_frameworks.minha_estante.dto.response.LivroResponse;
import com.backend_frameworks.minha_estante.enums.*;
import com.backend_frameworks.minha_estante.model.Livro;
import com.backend_frameworks.minha_estante.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    private final UUID livroId = UUID.randomUUID();

    private LivroRequest criarRequest() {
        return new LivroRequest(
                "O Hobbit",
                "J.R.R. Tolkien",
                310,
                150,
                Genero.values()[0],
                TipoLeitura.values()[0],
                StatusLeitura.LENDO
        );
    }

    private Livro criarLivro() {
        Livro livro = new Livro();
        livro.setId(livroId);
        livro.setTitulo("O Hobbit");
        livro.setAutor("J.R.R. Tolkien");
        livro.setPaginas(310);
        livro.setPaginaAtual(150);
        livro.setGenero(Genero.values()[0]);
        livro.setTipoLeitura(TipoLeitura.values()[0]);
        livro.setStatusLeitura(StatusLeitura.LENDO);
        return livro;
    }

    @Test
    void deveCadastrarLivroValido() {

        // Arrange
        LivroRequest request = criarRequest();

        when(livroRepository.save(any(Livro.class)))
                .thenAnswer(invocation -> {
                    Livro livro = invocation.getArgument(0);
                    livro.setId(livroId);
                    return livro;
                });

        // Act
        LivroResponse resposta = livroService.cadastrar(request);

        // Assert
        assertEquals("O Hobbit", resposta.titulo());
        assertEquals("J.R.R. Tolkien", resposta.autor());
        assertEquals(150, resposta.paginaAtual());
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    void deveListarLivros() {

        // Arrange
        when(livroRepository.findAll())
                .thenReturn(List.of(criarLivro()));

        // Act
        List<LivroResponse> livros = livroService.listar();

        // Assert
        assertEquals(1, livros.size());
        assertEquals("O Hobbit", livros.get(0).titulo());
        verify(livroRepository).findAll();
    }

    @Test
    void deveBuscarLivroPorId() {

        // Arrange
        when(livroRepository.findById(livroId))
                .thenReturn(Optional.of(criarLivro()));

        // Act
        LivroResponse resposta = livroService.buscarPorId(livroId);

        // Assert
        assertEquals(livroId, resposta.id());
        assertEquals("O Hobbit", resposta.titulo());
        verify(livroRepository).findById(livroId);
    }

    @Test
    void deveAtualizarLivro() {

        // Arrange
        Livro livro = criarLivro();

        when(livroRepository.findById(livroId))
                .thenReturn(Optional.of(livro));
        when(livroRepository.save(livro)).thenReturn(livro);

        LivroRequest request = new LivroRequest(
                "O Hobbit - Nova Edição",
                "John Ronald Reuel Tolkien",
                350,
                200,
                livro.getGenero(),
                livro.getTipoLeitura(),
                StatusLeitura.LENDO
        );

        // Act
        LivroResponse resposta = livroService.atualizar(livroId, request);

        // Assert
        assertEquals("O Hobbit - Nova Edição", resposta.titulo());
        assertEquals("John Ronald Reuel Tolkien", resposta.autor());
        assertEquals(350, resposta.paginas());
        assertEquals(200, resposta.paginaAtual());
        verify(livroRepository).save(livro);
    }

    @Test
    void deveExcluirLivro() {

        // Arrange
        Livro livro = criarLivro();

        when(livroRepository.findById(livroId))
                .thenReturn(Optional.of(livro));

        // Act
        livroService.excluir(livroId);

        // Assert
        verify(livroRepository).delete(livro);
    }

    @Test
    void deveRejeitarPaginaAtualMaiorQueTotal() {

        // Arrange
        LivroRequest request = new LivroRequest(
                "O Hobbit",
                "J.R.R. Tolkien",
                310,
                400,
                Genero.values()[0],
                TipoLeitura.values()[0],
                StatusLeitura.LENDO
        );

        // Act
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.cadastrar(request)
        );

        // Assert
        assertEquals(
                "A página atual deve estar entre zero e o total de páginas",
                erro.getMessage()
        );
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveRejeitarLivroInexistente() {

        // Arrange
        when(livroRepository.findById(livroId))
                .thenReturn(Optional.empty());

        // Act
        EntityNotFoundException erro = assertThrows(
                EntityNotFoundException.class,
                () -> livroService.buscarPorId(livroId)
        );

        // Assert
        assertEquals("Livro não encontrado", erro.getMessage());
        verify(livroRepository).findById(livroId);
    }

    @Test
    void deveAplicarValoresPadrao() {

        // Arrange
        LivroRequest request = new LivroRequest(
                "O Hobbit",
                "J.R.R. Tolkien",
                310,
                null,
                Genero.FANTASIA,
                TipoLeitura.FISICO,
                null
        );

        when(livroRepository.save(any(Livro.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LivroResponse resposta = livroService.cadastrar(request);

        // Assert
        assertEquals(0, resposta.paginaAtual());
        assertEquals(StatusLeitura.QUERO_LER, resposta.statusLeitura());
        verify(livroRepository).save(any(Livro.class));
    }
}
