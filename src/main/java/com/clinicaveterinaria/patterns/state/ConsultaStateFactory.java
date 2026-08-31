package com.clinicaveterinaria.patterns.state;

import com.clinicaveterinaria.entity.enums.StatusConsulta;

/**
 * Fábrica para instanciar o Estado correto a partir do StatusConsulta.
 */
public class ConsultaStateFactory {

    public static ConsultaState criarEstado(StatusConsulta status) {
        if (status == null) {
            return new AgendadaState();
        }
        return switch (status) {
            case AGENDADA -> new AgendadaState();
            case EM_ANDAMENTO -> new EmAndamentoState();
            case REALIZADA -> new RealizadaState();
            case PAGA -> new PagaState();
            case CANCELADA -> new CanceladaState();
        };
    }
}
