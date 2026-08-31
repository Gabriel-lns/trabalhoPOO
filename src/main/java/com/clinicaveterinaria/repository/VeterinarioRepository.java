package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Veterinario;
import java.util.List;
import java.util.Optional;

/**
 * Interface / Contrato de Persistência para Veterinários (DIP).
 */
public interface VeterinarioRepository {
    Veterinario salvar(Veterinario veterinario);
    Optional<Veterinario> buscarPorCrmv(String crmv);
    List<Veterinario> listarTodos();
}
