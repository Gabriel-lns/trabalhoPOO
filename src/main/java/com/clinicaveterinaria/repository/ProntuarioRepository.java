package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Prontuario;
import java.util.Optional;

/**
 * Interface / Contrato de Persistência para Prontuários (DIP).
 */
public interface ProntuarioRepository {
    Prontuario salvar(Prontuario prontuario);
    Optional<Prontuario> buscarPorAnimalId(int idAnimal, Animal animal);
    void adicionarRegistroClinico(int idProntuario, String registro);
}
