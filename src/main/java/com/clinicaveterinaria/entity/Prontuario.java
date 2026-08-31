package com.clinicaveterinaria.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidade Prontuario conforme modelado no Astah.
 * Mantém o histórico clínico imutável do animal (RN03, RN05, RN08).
 */
public class Prontuario {
    private int idProntuario;
    private LocalDate dataCriacao;
    private Animal animal;
    private final List<String> historico = new ArrayList<>();
    private final List<Exame> exames = new ArrayList<>();
    private final List<Vacina> vacinas = new ArrayList<>();

    public Prontuario() {
        this.dataCriacao = LocalDate.now();
    }

    public Prontuario(int idProntuario, LocalDate dataCriacao, Animal animal) {
        this.idProntuario = idProntuario;
        this.dataCriacao = dataCriacao != null ? dataCriacao : LocalDate.now();
        this.animal = animal;
        if (animal != null) {
            animal.setProntuario(this);
        }
    }

    /**
     * Adiciona um registro clínico imutável no histórico do animal.
     * Conforme diagrama de classes do Astah e RN05.
     */
    public void adicionarRegistro(String registro) {
        if (registro != null && !registro.trim().isEmpty()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String entradaFormatada = "[" + timestamp + "] " + registro.trim();
            this.historico.add(entradaFormatada);
        }
    }

    /**
     * Adiciona registro histórico direto (usado ao restaurar do banco).
     */
    public void carregarRegistroBruto(String registroCompleto) {
        if (registroCompleto != null && !registroCompleto.trim().isEmpty()) {
            this.historico.add(registroCompleto.trim());
        }
    }

    /**
     * Consulta todo o histórico clínico do animal.
     * Conforme diagrama de classes do Astah.
     */
    public List<String> consultarHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public void adicionarExame(Exame exame) {
        if (exame != null && !exames.contains(exame)) {
            exames.add(exame);
            exame.setIdProntuario(this.idProntuario);
        }
    }

    public void adicionarVacina(Vacina vacina) {
        if (vacina != null && !vacinas.contains(vacina)) {
            vacinas.add(vacina);
            vacina.setIdProntuario(this.idProntuario);
        }
    }

    // Getters e Setters
    public int getIdProntuario() {
        return idProntuario;
    }

    public void setIdProntuario(int idProntuario) {
        this.idProntuario = idProntuario;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public List<Exame> getExames() {
        return Collections.unmodifiableList(exames);
    }

    public List<Vacina> getVacinas() {
        return Collections.unmodifiableList(vacinas);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prontuario that)) return false;
        return idProntuario == that.idProntuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProntuario);
    }
}
