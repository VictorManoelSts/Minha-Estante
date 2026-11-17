package com.backend_frameworks.minha_estante.repository;

import com.backend_frameworks.minha_estante.enums.Genero;
import com.backend_frameworks.minha_estante.enums.StatusLeitura;
import com.backend_frameworks.minha_estante.enums.TipoLeitura;
import com.backend_frameworks.minha_estante.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LivroRepository extends JpaRepository<Livro, UUID> {

    Optional<Livro> findByUsuarioIdAndTitulo(UUID usuarioId, String titulo);

    // Listar todos os livros do usuário
    List<Livro> findByUsuarioId(UUID usuarioId);

    // Filtrar por gênero
    List<Livro> findByUsuarioIdAndGenero(
            UUID usuarioId, Genero genero
    );

    // Filtrar por tipo de leitura
    List<Livro> findByUsuarioIdAndTipoLeitura(
            UUID usuarioId, TipoLeitura tipoLeitura
    );

    // Filtrar por status de leitura
    List<Livro> findByUsuarioIdAndStatusLeitura(
            UUID usuarioId, StatusLeitura statusLeitura
    );
}
