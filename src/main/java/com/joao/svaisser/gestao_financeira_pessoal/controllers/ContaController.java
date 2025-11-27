package com.joao.svaisser.gestao_financeira_pessoal.controllers;

import com.joao.svaisser.gestao_financeira_pessoal.models.Conta;
import com.joao.svaisser.gestao_financeira_pessoal.services.ContaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/contas")
@CrossOrigin(origins = "*")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @GetMapping
    public ResponseEntity<List<Conta>> listarTodas() {
        try {
            List<Conta> contas = contaService.findAll();
            return ResponseEntity.ok(contas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conta> buscarPorId(@PathVariable Long id) {
        try {
            Optional<Conta> conta = contaService.findById(id);
            return conta.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Conta>> buscarPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Conta> contas = contaService.findByUsuarioId(usuarioId);
            return ResponseEntity.ok(contas);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody Conta conta) {
        try {
            Conta salva = contaService.create(conta);
            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Conta criada com sucesso");
            response.put("conta", salva);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Erro ao criar conta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable Long id, @Valid @RequestBody Conta conta) {
        try {
            Conta atualizada = contaService.update(id, conta);
            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Conta atualizada com sucesso");
            response.put("conta", atualizada);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Erro ao atualizar conta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable Long id) {
        try {
            contaService.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Conta deletada com sucesso");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", "Erro ao deletar conta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Map<String, String>> desativar(@PathVariable Long id) {
        try {
            contaService.desativarConta(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Conta desativada com sucesso");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", "Erro ao desativar conta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Map<String, String>> ativar(@PathVariable Long id) {
        try {
            contaService.ativarConta(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Conta ativada com sucesso");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", "Erro ao ativar conta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/{id}/adicionar-saldo")
    public ResponseEntity<Map<String, Object>> adicionarSaldo(@PathVariable Long id, @RequestParam BigDecimal valor) {
        try {
            contaService.adicionarSaldo(id, valor);
            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Saldo adicionado com sucesso");
            Optional<Conta> conta = contaService.findById(id);
            if (conta.isPresent()) {
                response.put("saldoAtual", conta.get().getSaldo());
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Erro ao adicionar saldo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/{id}/remover-saldo")
    public ResponseEntity<Map<String, Object>> removerSaldo(@PathVariable Long id, @RequestParam BigDecimal valor) {
        try {
            contaService.removerSaldo(id, valor);
            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Saldo removido com sucesso");
            Optional<Conta> conta = contaService.findById(id);
            if (conta.isPresent()) {
                response.put("saldoAtual", conta.get().getSaldo());
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Erro ao remover saldo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

