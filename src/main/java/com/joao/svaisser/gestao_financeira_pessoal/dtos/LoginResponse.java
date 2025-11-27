package com.joao.svaisser.gestao_financeira_pessoal.dtos;

public class LoginResponse {
    private String token;
    private Long usuarioId;
    private String email;
    private String nome;

    public LoginResponse(String token, Long usuarioId, String email, String nome) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.email = email;
        this.nome = nome;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

