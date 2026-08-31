package com.clinicaveterinaria.entity;

import java.util.Objects;

/**
 * Entidade Veterinario conforme modelado no Astah.
 * Representa o profissional habilitado para realizar atendimentos.
 */
public class Veterinario {
    private String crmv;
    private String nome;
    private String especialidade;
    private String telefone;

    public Veterinario() {
    }

    public Veterinario(String crmv, String nome, String especialidade, String telefone) {
        this.crmv = Objects.requireNonNull(crmv, "CRMV não pode ser nulo").trim();
        this.nome = Objects.requireNonNull(nome, "Nome não pode ser nulo").trim();
        this.especialidade = especialidade != null ? especialidade.trim() : "Clínica Geral";
        this.telefone = telefone != null ? telefone.trim() : "";
    }

    // Getters e Setters
    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    // Alias para suportar compatibilidade com o Astah onde foi grafado 'crvm'
    public String getCrvm() {
        return getCrmv();
    }

    public void setCrvm(String crvm) {
        setCrmv(crvm);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Veterinario that)) return false;
        return Objects.equals(crmv, that.crmv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(crmv);
    }

    @Override
    public String toString() {
        return "Dr(a). " + nome + " (CRMV: " + crmv + " - " + especialidade + ")";
    }
}
