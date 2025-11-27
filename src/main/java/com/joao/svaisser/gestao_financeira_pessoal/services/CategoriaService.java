package com.joao.svaisser.gestao_financeira_pessoal.services;

import com.joao.svaisser.gestao_financeira_pessoal.models.Categoria;
import com.joao.svaisser.gestao_financeira_pessoal.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da categoria deve ser um número positivo");
        }
        return categoriaRepository.findById(id);
    }

    public List<Categoria> findByTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo não pode ser vazio");
        }
        return categoriaRepository.findByTipo(tipo);
    }

    public Categoria create(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }

        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio");
        }

        if (categoriaRepository.findByNome(categoria.getNome()).isPresent()) {
            throw new IllegalArgumentException("Categoria com este nome já existe");
        }

        if (categoria.getTipo() == null) {
            throw new IllegalArgumentException("Tipo da categoria é obrigatório");
        }

        return categoriaRepository.save(categoria);
    }

    public Categoria update(Long id, Categoria categoriaAtualizada) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da categoria deve ser um número positivo");
        }

        if (categoriaAtualizada == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }

        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria com ID " + id + " não encontrada"));

        if (categoriaAtualizada.getNome() != null && !categoriaAtualizada.getNome().trim().isEmpty()) {
            // Verificar se novo nome já existe em outra categoria
            if (!categoriaAtualizada.getNome().equals(categoriaExistente.getNome())) {
                if (categoriaRepository.findByNome(categoriaAtualizada.getNome()).isPresent()) {
                    throw new IllegalArgumentException("Categoria com este nome já existe");
                }
            }
            categoriaExistente.setNome(categoriaAtualizada.getNome());
        } else if (categoriaAtualizada.getNome() != null) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }

        if (categoriaAtualizada.getTipo() != null) {
            categoriaExistente.setTipo(categoriaAtualizada.getTipo());
        }

        if (categoriaAtualizada.getDescricao() != null) {
            categoriaExistente.setDescricao(categoriaAtualizada.getDescricao());
        }

        if (categoriaAtualizada.getCor() != null && !categoriaAtualizada.getCor().trim().isEmpty()) {
            categoriaExistente.setCor(categoriaAtualizada.getCor());
        }

        if (categoriaAtualizada.getIcone() != null && !categoriaAtualizada.getIcone().trim().isEmpty()) {
            categoriaExistente.setIcone(categoriaAtualizada.getIcone());
        }

        return categoriaRepository.save(categoriaExistente);
    }

    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da categoria deve ser um número positivo");
        }

        if (!categoriaRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Categoria com ID " + id + " não encontrada");
        }

        categoriaRepository.deleteById(id);
    }

    public Optional<Categoria> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return categoriaRepository.findByNome(nome);
    }
}

