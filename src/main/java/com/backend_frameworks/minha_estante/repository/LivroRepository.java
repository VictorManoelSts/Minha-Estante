package com.backend_frameworks.minha_estante.repository;

import com.backend_frameworks.minha_estante.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LivroRepository extends JpaRepository<Livro, UUID> {
}
