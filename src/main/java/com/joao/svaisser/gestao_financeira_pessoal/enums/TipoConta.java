package com.joao.svaisser.gestao_financeira_pessoal.enums;

public enum TipoConta {
    CORRENTE("C", "Conta Corrente"),
    POUPANCA("P", "Conta Poupança"),
    INVESTIMENTO("I", "Conta de Investimento"),
    SALARIO("S", "Conta Salário");

    private final String value;
    private final String description;

    TipoConta(final String value, final String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static TipoConta fromValue(String dataValue) {
        for (TipoConta tipo : TipoConta.values()) {
            if (tipo.value.equals(dataValue)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + dataValue);
    }
}
