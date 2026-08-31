package com.clinicaveterinaria.entity.enums;

/**
 * Representa os estados do ciclo de vida de uma Consulta.
 * Mapeado diretamente do Diagrama de Máquina de Estados do Astah.
 */
public enum StatusConsulta {
    AGENDADA("Agendada"),
    EM_ANDAMENTO("Em Andamento"),
    REALIZADA("Realizada"),
    PAGA("Paga"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusConsulta fromString(String text) {
        if (text == null) return AGENDADA;
        for (StatusConsulta s : StatusConsulta.values()) {
            if (s.name().equalsIgnoreCase(text) || s.descricao.equalsIgnoreCase(text)) {
                return s;
            }
        }
        return AGENDADA;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
