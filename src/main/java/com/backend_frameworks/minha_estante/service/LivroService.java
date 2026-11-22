
package com.backend_frameworks.minha_estante.service;

import com.backend_frameworks.minha_estante.dto.request.LivroRequest;
import com.backend_frameworks.minha_estante.dto.response.LivroResponse;
import com.backend_frameworks.minha_estante.enums.StatusLeitura;
import com.backend_frameworks.minha_estante.model.Livro;
import com.backend_frameworks.minha_estante.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    @Transactional
    public LivroResponse cadastrar(LivroRequest request) {

        validarPaginas(request);

        Livro livro = new Livro();
        preencherDados(livro, request);

        return toResponse(livroRepository.save(livro));
    }

    @Transactional(readOnly = true)
    public List<LivroResponse> listar() {
        return livroRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LivroResponse buscarPorId(UUID livroId) {
        return toResponse(buscarLivro(livroId));
    }

    @Transactional
    public LivroResponse atualizar(UUID livroId, LivroRequest request) {

        Livro livro = buscarLivro(livroId);

        validarPaginas(request);
        preencherDados(livro, request);

        return toResponse(livroRepository.save(livro));
    }

    @Transactional
    public void excluir(UUID livroId) {
        Livro livro = buscarLivro(livroId);
        livroRepository.delete(livro);
    }

    private Livro buscarLivro(UUID livroId) {
        return livroRepository.findById(livroId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Livro não encontrado"));
    }

    private void validarPaginas(LivroRequest request) {

        if (request.paginas() == null || request.paginas() < 1) {
            throw new IllegalArgumentException(
                    "O total de páginas deve ser maior que zero");
        }

        int paginaAtual;
        if (request.paginaAtual() == null){
            paginaAtual = 0;
        }else{
            paginaAtual = request.paginaAtual();
        }
        if (paginaAtual < 0 || paginaAtual > request.paginas()) {
            throw new IllegalArgumentException(
                    "A página atual deve estar entre zero e o total de páginas");
        }
    }

    private void preencherDados(Livro livro, LivroRequest request) {
        livro.setTitulo(request.titulo());
        livro.setAutor(request.autor());
        livro.setPaginas(request.paginas());
        livro.setPaginaAtual(
                request.paginaAtual() == null ? 0 : request.paginaAtual()
        );
        livro.setGenero(request.genero());
        livro.setTipoLeitura(request.tipoLeitura());
        livro.setStatusLeitura(
                request.statusLeitura() == null
                        ? StatusLeitura.QUERO_LER
                        : request.statusLeitura()
        );
    }

    private LivroResponse toResponse(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getPaginas(),
                livro.getPaginaAtual(),
                livro.getGenero(),
                livro.getTipoLeitura(),
                livro.getStatusLeitura()
        );
    }
}
