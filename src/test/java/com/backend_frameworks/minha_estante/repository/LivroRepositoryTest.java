
package com.backend_frameworks.minha_estante.repository;

import com.backend_frameworks.minha_estante.enums.Genero;
import com.backend_frameworks.minha_estante.enums.StatusLeitura;
import com.backend_frameworks.minha_estante.enums.TipoLeitura;
import com.backend_frameworks.minha_estante.model.Livro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@TestPropertySource(properties = {
        "spring.datasource.url=${TEST_DB_URL}",
        "spring.datasource.username=${TEST_DB_USER}",
        "spring.datasource.password=${TEST_DB_PASSWORD}",
        "spring.jpa.hibernate.ddl-auto=update"
})
class LivroRepositoryTest {

    @Autowired
    private LivroRepository livroRepository;

    @BeforeEach
    void prepararDados() {

        livroRepository.saveAllAndFlush(List.of(
                criarLivro(
                        "O Hobbit",
                        "J.R.R. Tolkien",
                        Genero.FANTASIA,
                        TipoLeitura.FISICO,
                        StatusLeitura.LENDO
                ),
                criarLivro(
                        "A Sociedade do Anel",
                        "J.R.R. Tolkien",
                        Genero.FANTASIA,
                        TipoLeitura.FISICO,
                        StatusLeitura.LIDO
                ),
                criarLivro(
                        "Livro de Teste",
                        "Outro Autor",
                        Genero.FANTASIA,
                        TipoLeitura.FISICO,
                        StatusLeitura.QUERO_LER
                )
        ));
    }

    private Livro criarLivro(
            String titulo,
            String autor,
            Genero genero,
            TipoLeitura tipo,
            StatusLeitura status
    ) {
        Livro livro = new Livro();
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setPaginas(300);
        livro.setPaginaAtual(100);
        livro.setGenero(genero);
        livro.setTipoLeitura(tipo);
        livro.setStatusLeitura(status);

        return livro;
    }

    @Test
    void deveListarTodosOsLivros() {

        // Arrange
        // Três livros cadastrados no BeforeEach.

        // Act
        List<Livro> resultado = livroRepository.buscarComFiltros(
                null, null, null, null, null
        );

        // Assert
        assertEquals(3, resultado.size());
    }

    @Test
    void deveFiltrarPorTitulo() {

        // Arrange
        String titulo = "hobbit";

        // Act
        List<Livro> resultado = livroRepository.buscarComFiltros(
                titulo, null, null, null, null
        );

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("O Hobbit", resultado.get(0).getTitulo());
    }

    @Test
    void deveFiltrarPorAutor() {

        // Arrange
        String autor = "tolkien";

        // Act
        List<Livro> resultado = livroRepository.buscarComFiltros(
                null, autor, null, null, null
        );

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(
                livro -> livro.getAutor().equals("J.R.R. Tolkien")
        ));
    }

    @Test
    void deveFiltrarPorGenero() {

        // Arrange
        Genero genero = Genero.FANTASIA;

        // Act
        List<Livro> resultado = livroRepository.buscarComFiltros(
                null, null, genero, null, null
        );

        // Assert
        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().allMatch(
                livro -> livro.getGenero() == Genero.FANTASIA
        ));
    }

    @Test
    void deveFiltrarPorTipoLeitura() {

        // Arrange
        TipoLeitura tipo = TipoLeitura.FISICO;

        // Act
        List<Livro> resultado = livroRepository.buscarComFiltros(
                null, null, null, tipo, null
        );

        // Assert
        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().allMatch(
                livro -> livro.getTipoLeitura() == TipoLeitura.FISICO
        ));
    }

    @Test
    void deveFiltrarPorStatusLeitura() {

        // Arrange
        StatusLeitura status = StatusLeitura.LIDO;

        // Act
        List<Livro> resultado = livroRepository.buscarComFiltros(
                null, null, null, null, status
        );

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("A Sociedade do Anel",
                resultado.get(0).getTitulo());
    }

    @Test
    void deveCombinarAutorEStatus() {

        // Arrange
        String autor = "tolkien";
        StatusLeitura status = StatusLeitura.LENDO;

        // Act
        List<Livro> resultado = livroRepository.buscarComFiltros(
                null, autor, null, null, status
        );

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("O Hobbit",
                resultado.get(0).getTitulo());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoEncontrar() {

        // Arrange
        String titulo = "Livro inexistente 123";

        // Act
        List<Livro> resultado = livroRepository.buscarComFiltros(
                titulo, null, null, null, null
        );

        // Assert
        assertTrue(resultado.isEmpty());
    }
}
