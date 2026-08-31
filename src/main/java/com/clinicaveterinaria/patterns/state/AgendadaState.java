package com.clinicaveterinaria.patterns.state;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;

/**
 * Estado AGENDADA da Consulta.
 * Permite iniciar atendimento ou cancelar agendamento.
 */
public class AgendadaState implements ConsultaState {

    @Override
    public void agendar(Consulta consulta) {
        throw new IllegalStateException("A consulta já se encontra com status Agendada.");
    }

    @Override
    public void iniciar(Consulta consulta) {
        consulta.setEstadoInterno(new EmAndamentoState());
    }

    @Override
    public void finalizar(Consulta consulta, String observacoes) {
        throw new IllegalStateException("Não é possível finalizar uma consulta que ainda não foi iniciada.");
    }

    @Override
    public void cancelar(Consulta consulta) {
        consulta.setEstadoInterno(new CanceladaState());
    }

    @Override
    public void pagar(Consulta consulta, Pagamento pagamento) {
        throw new IllegalStateException("RN07: O pagamento só pode ser processado após a realização da consulta.");
    }

    @Override
    public StatusConsulta getStatus() {
        return StatusConsulta.AGENDADA;
    }

    @Override
    public String toString() {
        return StatusConsulta.AGENDADA.getDescricao();
    }
}
