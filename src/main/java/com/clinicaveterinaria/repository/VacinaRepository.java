package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Vacina;
import java.util.List;

/**
 * Interface / Contrato de Persistência para Vacinas (DIP).
 */
public interface VacinaRepository {
    Vacina salvar(Vacina vacina);
    List<Vacina> listarPorProntuario(int idProntuario);
    List<Vacina> listarPorConsulta(int idConsulta);
}
