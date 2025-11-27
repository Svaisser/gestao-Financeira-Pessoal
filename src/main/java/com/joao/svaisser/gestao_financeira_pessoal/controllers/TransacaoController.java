package com.joao.svaisser.gestao_financeira_pessoal.controllers;

import com.joao.svaisser.gestao_financeira_pessoal.models.Transacao;
import com.joao.svaisser.gestao_financeira_pessoal.services.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @GetMapping
    public ResponseEntity<List<Transacao>> listarTodas() {
        try {
            List<Transacao> transacoes = transacaoService.findAll();
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transacao> buscarPorId(@PathVariable Long id) {
        try {
            Optional<Transacao> transacao = transacaoService.findById(id);
            return transacao.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/conta/{contaId}")
    public ResponseEntity<List<Transacao>> buscarPorConta(@PathVariable Long contaId) {
        try {
            List<Transacao> transacoes = transacaoService.findByContaId(contaId);
            return ResponseEntity.ok(transacoes);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody Transacao transacao) {
        try {
            Transacao salva = transacaoService.create(transacao);
            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Transação criada com sucesso");
            response.put("transacao", salva);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Erro ao criar transação");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable Long id, @Valid @RequestBody Transacao transacao) {
        try {
            Transacao atualizada = transacaoService.update(id, transacao);
            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Transação atualizada com sucesso");
            response.put("transacao", atualizada);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Erro ao atualizar transação");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable Long id) {
        try {
            transacaoService.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Transação deletada com sucesso");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("erro", "Erro ao deletar transação");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

