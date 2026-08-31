package com.clinicaveterinaria.control;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Tutor;
import com.clinicaveterinaria.entity.Veterinario;
import com.clinicaveterinaria.repository.AnimalRepository;
import com.clinicaveterinaria.repository.RepositoryFactory;
import com.clinicaveterinaria.repository.TutorRepository;
import com.clinicaveterinaria.repository.VeterinarioRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de Cadastros Básicos (BCE).
 * Aplica Inversão de Dependência (DIP).
 */
public class ControladorCadastros {
    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;
    private final VeterinarioRepository veterinarioRepository;

    public ControladorCadastros() {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        this.tutorRepository = factory.getTutorRepository();
        this.animalRepository = factory.getAnimalRepository();
        this.veterinarioRepository = factory.getVeterinarioRepository();
    }

    public ControladorCadastros(TutorRepository tutorRepository, AnimalRepository animalRepository, VeterinarioRepository veterinarioRepository) {
        this.tutorRepository = tutorRepository;
        this.animalRepository = animalRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    public Tutor cadastrarTutor(String cpf, String nome, String telefone, String endereco) {
        if (cpf == null || cpf.trim().isEmpty() || nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF e Nome são obrigatórios para o cadastro de Tutor.");
        }
        Tutor tutor = new Tutor(cpf, nome, telefone, endereco);
        return tutorRepository.salvar(tutor);
    }

    public Animal cadastrarAnimal(String nome, String especie, String raca, LocalDate dataNascimento, String tutorCpf) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do animal é obrigatório.");
        }
        Tutor tutor = tutorRepository.buscarPorCpf(tutorCpf)
                .orElseThrow(() -> new IllegalArgumentException("RN01: Tutor com CPF " + tutorCpf + " não encontrado."));

        Animal animal = new Animal(0, nome, especie, raca, dataNascimento, tutor);
        return animalRepository.salvar(animal);
    }

    public Veterinario cadastrarVeterinario(String crmv, String nome, String especialidade, String telefone) {
        if (crmv == null || crmv.trim().isEmpty() || nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("CRMV e Nome são obrigatórios para o cadastro de Veterinário.");
        }
        Veterinario vet = new Veterinario(crmv, nome, especialidade, telefone);
        return veterinarioRepository.salvar(vet);
    }

    public List<Tutor> listarTutores() {
        return tutorRepository.listarTodos();
    }

    public List<Animal> listarAnimais() {
        return animalRepository.listarTodos();
    }

    public List<Veterinario> listarVeterinarios() {
        return veterinarioRepository.listarTodos();
    }
}
