package com.joao.svaisser.gestao_financeira_pessoal.repositories;

import com.joao.svaisser.gestao_financeira_pessoal.models.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByContaId(Long contaId);
    List<Transacao> findByContaIdAndDataBetween(Long contaId, LocalDateTime dataInicio, LocalDateTime dataFim);
}

