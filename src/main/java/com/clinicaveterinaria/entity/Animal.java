package com.clinicaveterinaria.entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Entidade Animal conforme modelado no Astah.
 * Representa o paciente da clínica veterinária.
 */
public class Animal {
    private int idAnimal;
    private String nome;
    private String especie;
    private String raca;
    private LocalDate dataNascimento;
    private Tutor tutor;
    private Prontuario prontuario;

    public Animal() {
    }

    public Animal(int idAnimal, String nome, String especie, String raca, LocalDate dataNascimento, Tutor tutor) {
        this.idAnimal = idAnimal;
        this.nome = Objects.requireNonNull(nome, "Nome do animal não pode ser nulo").trim();
        this.especie = especie != null ? especie.trim() : "Canina";
        this.raca = raca != null ? raca.trim() : "SRD (Sem Raça Definida)";
        this.dataNascimento = dataNascimento != null ? dataNascimento : LocalDate.now();
        this.tutor = tutor;
        if (tutor != null) {
            tutor.adicionarAnimal(this);
        }
    }

    /**
     * Calcula a idade do animal em anos com base na data de nascimento.
     * Conforme especificação do diagrama de classes do Astah.
     */
    public int obterIdade() {
        if (dataNascimento == null) {
            return 0;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    // Getters e Setters
    public int getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }

    public void setProntuario(Prontuario prontuario) {
        this.prontuario = prontuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Animal animal)) return false;
        return idAnimal == animal.idAnimal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAnimal);
    }

    @Override
    public String toString() {
        return nome + " (" + especie + " - " + raca + ", " + obterIdade() + " anos)";
    }
}
