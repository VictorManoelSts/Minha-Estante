package com.backend_frameworks.minha_estante.model;

import com.backend_frameworks.minha_estante.enums.StatusLeitura;
import com.backend_frameworks.minha_estante.enums.TipoLeitura;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 256)
    private String nome;

    @Column(nullable = false, length = 4)
    private Integer paginas;

    @Column(length = 256)
    private String genero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_leitura", nullable = false, length = 20)
    private TipoLeitura tipoLeitura;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_leitura", nullable = false, length = 20)
    @Builder.Default
    private StatusLeitura statusLeitura = StatusLeitura.LENDO;

}
