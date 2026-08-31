package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Animal;
import java.util.List;
import java.util.Optional;

/**
 * Interface / Contrato de Persistência para Animais (DIP).
 */
public interface AnimalRepository {
    Animal salvar(Animal animal);
    Optional<Animal> buscarPorId(int idAnimal);
    List<Animal> listarTodos();
    List<Animal> listarPorTutor(String tutorCpf);
}
