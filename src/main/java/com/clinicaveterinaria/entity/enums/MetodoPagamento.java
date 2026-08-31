package com.clinicaveterinaria.entity.enums;

/**
 * Métodos de pagamento aceitos na clínica.
 */
public enum MetodoPagamento {
    PIX("PIX"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
    DINHEIRO("Dinheiro em Espécie");

    private final String descricao;

    MetodoPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static MetodoPagamento fromString(String text) {
        if (text == null) return PIX;
        for (MetodoPagamento m : MetodoPagamento.values()) {
            if (m.name().equalsIgnoreCase(text) || m.descricao.equalsIgnoreCase(text)) {
                return m;
            }
        }
        return PIX;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
