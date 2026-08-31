package com.clinicaveterinaria.patterns.state;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;

/**
 * Estado REALIZADA da Consulta.
 * Atendimento médico concluído, liberada para faturamento e pagamento na recepção/caixa.
 */
public class RealizadaState implements ConsultaState {

    @Override
    public void agendar(Consulta consulta) {
        throw new IllegalStateException("Esta consulta já foi realizada.");
    }

    @Override
    public void iniciar(Consulta consulta) {
        throw new IllegalStateException("Esta consulta já foi finalizada pelo veterinário.");
    }

    @Override
    public void finalizar(Consulta consulta, String observacoes) {
        throw new IllegalStateException("Esta consulta já está concluída.");
    }

    @Override
    public void cancelar(Consulta consulta) {
        throw new IllegalStateException("Consultas já realizadas não podem ser canceladas.");
    }

    @Override
    public void pagar(Consulta consulta, Pagamento pagamento) {
        if (pagamento == null) {
            throw new IllegalArgumentException("Instância de pagamento inválida.");
        }
        consulta.setPagamento(pagamento);
        consulta.setEstadoInterno(new PagaState());
    }

    @Override
    public StatusConsulta getStatus() {
        return StatusConsulta.REALIZADA;
    }

    @Override
    public String toString() {
        return StatusConsulta.REALIZADA.getDescricao();
    }
}
