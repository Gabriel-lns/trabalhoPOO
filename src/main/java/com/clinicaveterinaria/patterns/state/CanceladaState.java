package com.clinicaveterinaria.patterns.state;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;

/**
 * Estado CANCELADA da Consulta.
 * Estado terminal de cancelamento.
 */
public class CanceladaState implements ConsultaState {

    @Override
    public void agendar(Consulta consulta) {
        throw new IllegalStateException("Esta consulta foi cancelada.");
    }

    @Override
    public void iniciar(Consulta consulta) {
        throw new IllegalStateException("Consultas canceladas não podem ser iniciadas.");
    }

    @Override
    public void finalizar(Consulta consulta, String observacoes) {
        throw new IllegalStateException("Consultas canceladas não podem ser finalizadas.");
    }

    @Override
    public void cancelar(Consulta consulta) {
        throw new IllegalStateException("A consulta já está cancelada.");
    }

    @Override
    public void pagar(Consulta consulta, Pagamento pagamento) {
        throw new IllegalStateException("Consultas canceladas não aceitam pagamento.");
    }

    @Override
    public StatusConsulta getStatus() {
        return StatusConsulta.CANCELADA;
    }

    @Override
    public String toString() {
        return StatusConsulta.CANCELADA.getDescricao();
    }
}
