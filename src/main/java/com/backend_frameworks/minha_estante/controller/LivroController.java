
package com.backend_frameworks.minha_estante.controller;

import com.backend_frameworks.minha_estante.dto.request.LivroRequest;
import com.backend_frameworks.minha_estante.dto.response.LivroResponse;
import com.backend_frameworks.minha_estante.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend_frameworks.minha_estante.enums.Genero;
import com.backend_frameworks.minha_estante.enums.TipoLeitura;
import com.backend_frameworks.minha_estante.enums.StatusLeitura;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    public ResponseEntity<LivroResponse> cadastrar(
            @Valid @RequestBody LivroRequest request) {

        LivroResponse resposta = livroService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }


    @GetMapping
    public ResponseEntity<List<LivroResponse>> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) Genero genero,
            @RequestParam(required = false) TipoLeitura tipoLeitura,
            @RequestParam(required = false) StatusLeitura statusLeitura
    ) {
        return ResponseEntity.ok(
                livroService.listar(
                        titulo,
                        autor,
                        genero,
                        tipoLeitura,
                        statusLeitura
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscarPorId(
            @PathVariable UUID id) {

        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody LivroRequest request) {

        return ResponseEntity.ok(livroService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        livroService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
