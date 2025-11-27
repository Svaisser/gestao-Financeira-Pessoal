package com.joao.svaisser.gestao_financeira_pessoal.repositories;

import com.joao.svaisser.gestao_financeira_pessoal.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNome(String nome);
    List<Categoria> findByTipo(String tipo);
}
