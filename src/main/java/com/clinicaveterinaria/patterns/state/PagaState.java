package com.clinicaveterinaria.patterns.state;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;

/**
 * Estado PAGA da Consulta.
 * Estado final consolidado financeiramente e clinicamente.
 */
public class PagaState implements ConsultaState {

    @Override
    public void agendar(Consulta consulta) {
        throw new IllegalStateException("Esta consulta já foi concluída e paga.");
    }

    @Override
    public void iniciar(Consulta consulta) {
        throw new IllegalStateException("Esta consulta já foi finalizada.");
    }

    @Override
    public void finalizar(Consulta consulta, String observacoes) {
        throw new IllegalStateException("Esta consulta já foi finalizada e liquidada.");
    }

    @Override
    public void cancelar(Consulta consulta) {
        throw new IllegalStateException("Consultas pagas não podem ser canceladas.");
    }

    @Override
    public void pagar(Consulta consulta, Pagamento pagamento) {
        throw new IllegalStateException("Esta consulta já possui pagamento quitado.");
    }

    @Override
    public StatusConsulta getStatus() {
        return StatusConsulta.PAGA;
    }

    @Override
    public String toString() {
        return StatusConsulta.PAGA.getDescricao();
    }
}
