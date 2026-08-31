package com.clinicaveterinaria.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidade Tutor conforme modelado no Astah.
 * Representa o responsável legal pelo animal.
 */
public class Tutor {
    private String cpf;
    private String nome;
    private String telefone;
    private String endereco;
    private final List<Animal> animais = new ArrayList<>();

    public Tutor() {
    }

    public Tutor(String cpf, String nome, String telefone, String endereco) {
        this.cpf = Objects.requireNonNull(cpf, "CPF não pode ser nulo").trim();
        this.nome = Objects.requireNonNull(nome, "Nome não pode ser nulo").trim();
        this.telefone = telefone != null ? telefone.trim() : "";
        this.endereco = endereco != null ? endereco.trim() : "";
    }

    /**
     * Retorna os dados de contato formatados do Tutor.
     * Conforme assinatura no diagrama de classes do Astah.
     */
    public String obterDadosContato() {
        return String.format("Tutor: %s | Tel: %s | Endereço: %s | CPF: %s",
                nome,
                telefone.isEmpty() ? "Não informado" : telefone,
                endereco.isEmpty() ? "Não informado" : endereco,
                cpf);
    }

    public void adicionarAnimal(Animal animal) {
        if (animal != null && !animais.contains(animal)) {
            animais.add(animal);
            if (animal.getTutor() != this) {
                animal.setTutor(this);
            }
        }
    }

    public void removerAnimal(Animal animal) {
        if (animal != null) {
            animais.remove(animal);
        }
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<Animal> getAnimais() {
        return Collections.unmodifiableList(animais);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tutor tutor)) return false;
        return Objects.equals(cpf, tutor.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return nome + " (CPF: " + cpf + ")";
    }
}
