package com.backend_frameworks.minha_estante.model;

import com.backend_frameworks.minha_estante.enums.Genero;
import com.backend_frameworks.minha_estante.enums.StatusLeitura;
import com.backend_frameworks.minha_estante.enums.TipoLeitura;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "livros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 256)
    private String titulo;

    @Column(nullable = false, length = 256)
    private String autor;

    @Min(1)
    @Column(nullable = false)
    private Integer paginas;

    @Min(0)
    @Column(name = "pagina_atual")
    @Builder.Default
    private Integer paginaAtual = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Genero genero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_leitura", nullable = false, length = 20)
    private TipoLeitura tipoLeitura;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_leitura", nullable = false, length = 20)
    @Builder.Default
    private StatusLeitura statusLeitura = StatusLeitura.QUERO_LER;

}
