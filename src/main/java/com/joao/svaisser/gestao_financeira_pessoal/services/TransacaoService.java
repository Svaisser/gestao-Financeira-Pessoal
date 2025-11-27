package com.joao.svaisser.gestao_financeira_pessoal.services;

import com.joao.svaisser.gestao_financeira_pessoal.models.Transacao;
import com.joao.svaisser.gestao_financeira_pessoal.models.Conta;
import com.joao.svaisser.gestao_financeira_pessoal.models.Categoria;
import com.joao.svaisser.gestao_financeira_pessoal.repositories.TransacaoRepository;
import com.joao.svaisser.gestao_financeira_pessoal.repositories.ContaRepository;
import com.joao.svaisser.gestao_financeira_pessoal.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Transacao> findAll() {
        return transacaoRepository.findAll();
    }

    public Optional<Transacao> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da transação deve ser um número positivo");
        }
        return transacaoRepository.findById(id);
    }

    public List<Transacao> findByContaId(Long contaId) {
        if (contaId == null || contaId <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser um número positivo");
        }
        return transacaoRepository.findByContaId(contaId);
    }

    public Transacao create(Transacao transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transação não pode ser nula");
        }

        if (transacao.getDescricao() == null || transacao.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição da transação não pode ser vazia");
        }

        if (transacao.getValor() == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }

        if (transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }

        if (transacao.getTipo() == null) {
            throw new IllegalArgumentException("Tipo da transação é obrigatório");
        }

        if (transacao.getData() == null) {
            throw new IllegalArgumentException("Data da transação é obrigatória");
        }

        if (transacao.getData().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data não pode ser futura");
        }

        if (transacao.getCategorias() == null || transacao.getCategorias().isEmpty()) {
            throw new IllegalArgumentException("Transação deve ter pelo menos uma categoria");
        }

        if (transacao.getConta() == null || transacao.getConta().getId() == null) {
            throw new IllegalArgumentException("Conta é obrigatória");
        }

        Conta conta = contaRepository.findById(transacao.getConta().getId())
                .orElseThrow(() -> new IllegalArgumentException("Conta com ID " + transacao.getConta().getId() + " não encontrada"));

        if (!conta.getAtiva()) {
            throw new IllegalArgumentException("Não é possível criar transação em conta inativa");
        }

        // Verificar categorias
        for (Categoria categoria : transacao.getCategorias()) {
            if (!categoriaRepository.existsById(categoria.getId())) {
                throw new IllegalArgumentException("Categoria com ID " + categoria.getId() + " não encontrada");
            }
        }

        transacao.setConta(conta);
        transacao.setDataCriacao(LocalDateTime.now());

        // Atualizar saldo da conta
        if ("RECEITA".equals(transacao.getTipo())) {
            conta.setSaldo(conta.getSaldo().add(transacao.getValor()));
        } else if ("DESPESA".equals(transacao.getTipo())) {
            if (conta.getSaldo().compareTo(transacao.getValor()) < 0) {
                throw new IllegalArgumentException("Saldo insuficiente para realizar esta despesa");
            }
            conta.setSaldo(conta.getSaldo().subtract(transacao.getValor()));
        }

        contaRepository.save(conta);
        return transacaoRepository.save(transacao);
    }

    public Transacao update(Long id, Transacao transacaoAtualizada) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da transação deve ser um número positivo");
        }

        if (transacaoAtualizada == null) {
            throw new IllegalArgumentException("Transação não pode ser nula");
        }

        Transacao transacaoExistente = transacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação com ID " + id + " não encontrada"));

        if (transacaoAtualizada.getDescricao() != null && !transacaoAtualizada.getDescricao().trim().isEmpty()) {
            transacaoExistente.setDescricao(transacaoAtualizada.getDescricao());
        } else if (transacaoAtualizada.getDescricao() != null) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }

        if (transacaoAtualizada.getValor() != null) {
            if (transacaoAtualizada.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor deve ser positivo");
            }

            // Recalcular saldo da conta
            Conta conta = transacaoExistente.getConta();
            BigDecimal diferenca = transacaoAtualizada.getValor().subtract(transacaoExistente.getValor());

            if ("RECEITA".equals(transacaoExistente.getTipo())) {
                conta.setSaldo(conta.getSaldo().add(diferenca));
            } else if ("DESPESA".equals(transacaoExistente.getTipo())) {
                if (conta.getSaldo().compareTo(diferenca) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente para atualizar esta despesa");
                }
                conta.setSaldo(conta.getSaldo().subtract(diferenca));
            }

            contaRepository.save(conta);
            transacaoExistente.setValor(transacaoAtualizada.getValor());
        }

        if (transacaoAtualizada.getTipo() != null) {
            transacaoExistente.setTipo(transacaoAtualizada.getTipo());
        }

        if (transacaoAtualizada.getData() != null) {
            if (transacaoAtualizada.getData().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Data não pode ser futura");
            }
            transacaoExistente.setData(transacaoAtualizada.getData());
        }

        if (transacaoAtualizada.getObservacoes() != null) {
            transacaoExistente.setObservacoes(transacaoAtualizada.getObservacoes());
        }

        if (transacaoAtualizada.getCategorias() != null && !transacaoAtualizada.getCategorias().isEmpty()) {
            transacaoExistente.setCategorias(transacaoAtualizada.getCategorias());
        }

        return transacaoRepository.save(transacaoExistente);
    }

    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da transação deve ser um número positivo");
        }

        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação com ID " + id + " não encontrada"));

        Conta conta = transacao.getConta();

        // Reverter saldo da conta
        if ("RECEITA".equals(transacao.getTipo())) {
            conta.setSaldo(conta.getSaldo().subtract(transacao.getValor()));
        } else if ("DESPESA".equals(transacao.getTipo())) {
            conta.setSaldo(conta.getSaldo().add(transacao.getValor()));
        }

        contaRepository.save(conta);
        transacaoRepository.deleteById(id);
    }
}

