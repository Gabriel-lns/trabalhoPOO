package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface / Contrato de Persistência para Consultas (DIP).
 */
public interface ConsultaRepository {
    Consulta salvar(Consulta consulta);
    Consulta atualizar(Consulta consulta);
    Optional<Consulta> buscarPorId(int idConsulta);
    List<Consulta> listarTodas();
    List<Consulta> listarPorStatus(StatusConsulta status);
    List<Consulta> buscarPorVeterinarioEPeriodo(String crmv, LocalDateTime inicio, LocalDateTime fim);
}
