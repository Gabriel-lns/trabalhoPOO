package com.clinicaveterinaria.patterns.state;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;

/**
 * Interface do Padrão de Projeto Comportamental STATE (GoF).
 * Define as operações permitidas no ciclo de vida de uma Consulta.
 */
public interface ConsultaState {
    void agendar(Consulta consulta);
    void iniciar(Consulta consulta);
    void finalizar(Consulta consulta, String observacoes);
    void cancelar(Consulta consulta);
    void pagar(Consulta consulta, Pagamento pagamento);
    StatusConsulta getStatus();
}
