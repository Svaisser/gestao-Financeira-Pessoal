package com.joao.svaisser.gestao_financeira_pessoal.enums;

public enum TipoTransacao {
    RECEITA("R", "Receita gerada"),
    DESPESA("D", "Despesa gerada");

    private final String value;
    private final String description;

    TipoTransacao(final String value, final String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static TipoTransacao fromValue(String dataValue) {
        for (TipoTransacao tipo : TipoTransacao.values()) {
            if (tipo.value.equals(dataValue)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + dataValue);
    }
}
