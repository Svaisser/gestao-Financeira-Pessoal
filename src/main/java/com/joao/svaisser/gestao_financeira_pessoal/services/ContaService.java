package com.joao.svaisser.gestao_financeira_pessoal.services;

import com.joao.svaisser.gestao_financeira_pessoal.models.Conta;
import com.joao.svaisser.gestao_financeira_pessoal.models.Usuario;
import com.joao.svaisser.gestao_financeira_pessoal.repositories.ContaRepository;
import com.joao.svaisser.gestao_financeira_pessoal.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Conta> findAll() {
        return contaRepository.findAll();
    }

    public Optional<Conta> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser um número positivo");
        }
        return contaRepository.findById(id);
    }

    public List<Conta> findByUsuarioId(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser um número positivo");
        }
        return contaRepository.findByUsuarioId(usuarioId);
    }

    public Conta create(Conta conta) {
        if (conta == null) {
            throw new IllegalArgumentException("Conta não pode ser nula");
        }

        if (conta.getNome() == null || conta.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da conta não pode ser vazio");
        }

        if (conta.getTipo() == null) {
            throw new IllegalArgumentException("Tipo da conta é obrigatório");
        }

        if (conta.getSaldo() == null) {
            throw new IllegalArgumentException("Saldo não pode ser nulo");
        }

        if (conta.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Saldo não pode ser negativo");
        }

        if (conta.getMoeda() == null || conta.getMoeda().trim().isEmpty()) {
            throw new IllegalArgumentException("Moeda não pode ser vazia");
        }

        if (conta.getUsuario() == null || conta.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório");
        }

        Usuario usuario = usuarioRepository.findById(conta.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário com ID " + conta.getUsuario().getId() + " não encontrado"));

        if (!usuario.getAtivo()) {
            throw new IllegalArgumentException("Não é possível criar conta para usuário inativo");
        }

        conta.setUsuario(usuario);
        conta.setAtiva(true);
        return contaRepository.save(conta);
    }

    public Conta update(Long id, Conta contaAtualizada) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser um número positivo");
        }

        if (contaAtualizada == null) {
            throw new IllegalArgumentException("Conta não pode ser nula");
        }

        Conta contaExistente = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta com ID " + id + " não encontrada"));

        if (contaAtualizada.getNome() != null && !contaAtualizada.getNome().trim().isEmpty()) {
            contaExistente.setNome(contaAtualizada.getNome());
        } else if (contaAtualizada.getNome() != null) {
            throw new IllegalArgumentException("Nome da conta não pode ser vazio");
        }

        if (contaAtualizada.getTipo() != null) {
            contaExistente.setTipo(contaAtualizada.getTipo());
        }

        if (contaAtualizada.getSaldo() != null) {
            if (contaAtualizada.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Saldo não pode ser negativo");
            }
            contaExistente.setSaldo(contaAtualizada.getSaldo());
        }

        if (contaAtualizada.getMoeda() != null && !contaAtualizada.getMoeda().trim().isEmpty()) {
            contaExistente.setMoeda(contaAtualizada.getMoeda());
        } else if (contaAtualizada.getMoeda() != null) {
            throw new IllegalArgumentException("Moeda não pode ser vazia");
        }

        if (contaAtualizada.getAtiva() != null) {
            contaExistente.setAtiva(contaAtualizada.getAtiva());
        }

        return contaRepository.save(contaExistente);
    }

    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser um número positivo");
        }

        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta com ID " + id + " não encontrada"));

        if (conta.getTransacoes() != null && !conta.getTransacoes().isEmpty()) {
            throw new IllegalArgumentException("Não é possível deletar uma conta com transações");
        }

        contaRepository.deleteById(id);
    }

    public void desativarConta(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta com ID " + id + " não encontrada"));
        conta.setAtiva(false);
        contaRepository.save(conta);
    }

    public void ativarConta(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta com ID " + id + " não encontrada"));

        if (!conta.getUsuario().getAtivo()) {
            throw new IllegalArgumentException("Não é possível ativar conta de usuário inativo");
        }

        conta.setAtiva(true);
        contaRepository.save(conta);
    }

    public void adicionarSaldo(Long id, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }

        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta com ID " + id + " não encontrada"));

        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
    }

    public void removerSaldo(Long id, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }

        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta com ID " + id + " não encontrada"));

        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);
    }
}

