package com.clinicaveterinaria.patterns.state;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;

/**
 * Estado EM_ANDAMENTO da Consulta.
 * O veterinário está atendendo o paciente no consultório.
 */
public class EmAndamentoState implements ConsultaState {

    @Override
    public void agendar(Consulta consulta) {
        throw new IllegalStateException("A consulta já está em andamento.");
    }

    @Override
    public void iniciar(Consulta consulta) {
        throw new IllegalStateException("A consulta já está em andamento no consultório.");
    }

    @Override
    public void finalizar(Consulta consulta, String observacoes) {
        if (observacoes != null && !observacoes.trim().isEmpty()) {
            consulta.setObservacoes(observacoes.trim());
        }
        consulta.setEstadoInterno(new RealizadaState());
    }

    @Override
    public void cancelar(Consulta consulta) {
        throw new IllegalStateException("Não é possível cancelar uma consulta que já está em andamento pelo veterinário.");
    }

    @Override
    public void pagar(Consulta consulta, Pagamento pagamento) {
        throw new IllegalStateException("RN07: O atendimento ainda não foi concluído pelo veterinário.");
    }

    @Override
    public StatusConsulta getStatus() {
        return StatusConsulta.EM_ANDAMENTO;
    }

    @Override
    public String toString() {
        return StatusConsulta.EM_ANDAMENTO.getDescricao();
    }
}
