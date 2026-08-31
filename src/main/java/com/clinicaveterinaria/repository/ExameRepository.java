package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Exame;
import java.util.List;

/**
 * Interface / Contrato de Persistência para Exames (DIP).
 */
public interface ExameRepository {
    Exame salvar(Exame exame);
    List<Exame> listarPorProntuario(int idProntuario);
    List<Exame> listarPorConsulta(int idConsulta);
}
