package com.clinicaveterinaria.entity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade Vacina conforme modelado no Astah.
 * Representa as imunizações prescritas e aplicadas no animal.
 */
public class Vacina {
    private int idVacina;
    private String nome;
    private LocalDate dataAplicacao;
    private LocalDate proximaDose;
    private int idConsultaOrigem;
    private int idProntuario;

    public Vacina() {
    }

    public Vacina(int idVacina, String nome, LocalDate dataAplicacao, LocalDate proximaDose, int idConsultaOrigem, int idProntuario) {
        this.idVacina = idVacina;
        this.nome = Objects.requireNonNull(nome, "Nome da vacina não pode ser nulo").trim();
        this.dataAplicacao = dataAplicacao != null ? dataAplicacao : LocalDate.now();
        this.proximaDose = proximaDose;
        this.idConsultaOrigem = idConsultaOrigem;
        this.idProntuario = idProntuario;
    }

    // Getters e Setters
    public int getIdVacina() {
        return idVacina;
    }

    public void setIdVacina(int idVacina) {
        this.idVacina = idVacina;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }

    public void setDataAplicacao(LocalDate dataAplicacao) {
        this.dataAplicacao = dataAplicacao;
    }

    public LocalDate getProximaDose() {
        return proximaDose;
    }

    public void setProximaDose(LocalDate proximaDose) {
        this.proximaDose = proximaDose;
    }

    public int getIdConsultaOrigem() {
        return idConsultaOrigem;
    }

    public void setIdConsultaOrigem(int idConsultaOrigem) {
        this.idConsultaOrigem = idConsultaOrigem;
    }

    public int getIdProntuario() {
        return idProntuario;
    }

    public void setIdProntuario(int idProntuario) {
        this.idProntuario = idProntuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vacina vacina)) return false;
        return idVacina == vacina.idVacina;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVacina);
    }

    @Override
    public String toString() {
        return "Vacina: " + nome + " (Aplicada em: " + dataAplicacao + (proximaDose != null ? " | Próx. Dose: " + proximaDose : "") + ")";
    }
}
