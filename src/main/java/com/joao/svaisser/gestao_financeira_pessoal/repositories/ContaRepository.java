package com.joao.svaisser.gestao_financeira_pessoal.repositories;

import com.joao.svaisser.gestao_financeira_pessoal.models.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByUsuarioId(Long usuarioId);
    Optional<Conta> findByIdAndUsuarioId(Long id, Long usuarioId);
}

