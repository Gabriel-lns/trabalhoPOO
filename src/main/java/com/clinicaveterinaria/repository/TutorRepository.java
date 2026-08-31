package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Tutor;
import java.util.List;
import java.util.Optional;

/**
 * Interface / Contrato de Persistência para Tutores (DIP).
 */
public interface TutorRepository {
    Tutor salvar(Tutor tutor);
    Optional<Tutor> buscarPorCpf(String cpf);
    List<Tutor> listarTodos();
    boolean deletar(String cpf);
}
