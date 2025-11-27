package com.joao.svaisser.gestao_financeira_pessoal.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Set;

@Entity
@Table(name = "categoria", uniqueConstraints = {@UniqueConstraint(columnNames = "nome")})
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da categoria não pode ser vazio")
    @Column(nullable = false, unique = true)
    private String nome;

    @NotBlank(message = "Tipo da categoria não pode ser vazio")
    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "descricao")
    private String descricao;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$", message = "Cor deve ser hexadecimal válida (ex: #FF5733)")
    @Column(name = "cor")
    private String cor;

    @Column(name = "icone")
    private String icone;

    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    private Set<Transacao> transacoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public Set<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(Set<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
}
