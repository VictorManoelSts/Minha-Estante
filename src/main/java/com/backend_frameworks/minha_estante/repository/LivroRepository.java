
package com.backend_frameworks.minha_estante.repository;

import com.backend_frameworks.minha_estante.enums.Genero;
import com.backend_frameworks.minha_estante.enums.StatusLeitura;
import com.backend_frameworks.minha_estante.enums.TipoLeitura;
import com.backend_frameworks.minha_estante.model.Livro;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public interface LivroRepository extends
        JpaRepository<Livro, UUID>,
        JpaSpecificationExecutor<Livro> {

    default List<Livro> buscarComFiltros(
            String titulo,
            String autor,
            Genero genero,
            TipoLeitura tipoLeitura,
            StatusLeitura statusLeitura
    ) {

        Specification<Livro> filtros = (root, query, cb) -> {

            List<Predicate> condicoes = new ArrayList<>();

            if (titulo != null && !titulo.isBlank()) {
                condicoes.add(
                        cb.like(
                                cb.lower(root.get("titulo")),
                                "%" + titulo.toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            if (autor != null && !autor.isBlank()) {
                condicoes.add(
                        cb.like(
                                cb.lower(root.get("autor")),
                                "%" + autor.toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            if (genero != null) {
                condicoes.add(
                        cb.equal(root.get("genero"), genero)
                );
            }

            if (tipoLeitura != null) {
                condicoes.add(
                        cb.equal(root.get("tipoLeitura"), tipoLeitura)
                );
            }

            if (statusLeitura != null) {
                condicoes.add(
                        cb.equal(root.get("statusLeitura"), statusLeitura)
                );
            }

            return cb.and(condicoes.toArray(new Predicate[0]));
        };

        return findAll(filtros);
    }
}
