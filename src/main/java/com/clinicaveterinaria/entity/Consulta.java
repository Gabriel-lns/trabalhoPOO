package com.clinicaveterinaria.entity;

import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.patterns.state.AgendadaState;
import com.clinicaveterinaria.patterns.state.ConsultaState;
import com.clinicaveterinaria.patterns.state.ConsultaStateFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidade central Consulta conforme modelado no Astah.
 * O ciclo de vida e regras de transição de estado são geridos pelo Padrão STATE (GoF).
 */
public class Consulta {
    private int idConsulta;
    private LocalDateTime dataHora;
    private ConsultaState estado;
    private double valor;
    private String observacoes;
    private Animal animal;
    private Veterinario veterinario;
    private Pagamento pagamento;
    private final List<Exame> exames = new ArrayList<>();
    private final List<Vacina> vacinas = new ArrayList<>();

    public Consulta() {
        this.estado = new AgendadaState();
        this.dataHora = LocalDateTime.now();
        this.valor = 150.0; // Valor padrão de consulta clínica
        this.observacoes = "";
    }

    public Consulta(int idConsulta, LocalDateTime dataHora, double valor, Animal animal, Veterinario veterinario) {
        this.idConsulta = idConsulta;
        this.dataHora = dataHora != null ? dataHora : LocalDateTime.now();
        this.valor = valor >= 0 ? valor : 150.0;
        this.animal = animal;
        this.veterinario = veterinario;
        this.estado = new AgendadaState();
        this.observacoes = "";
    }

    // ==========================================
    // Métodos de Ciclo de Vida (Padrão STATE)
    // Conforme Diagrama de Classes e Estados
    // ==========================================

    /**
     * Agenda a consulta.
     * Conforme diagrama de classes do Astah.
     */
    public boolean agendar() {
        try {
            this.estado.agendar(this);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Inicia o atendimento clínico da consulta.
     * Conforme diagrama de classes do Astah.
     */
    public void iniciar() {
        this.estado.iniciar(this);
    }

    /**
     * Finaliza o atendimento clínico.
     * Conforme diagrama de classes do Astah.
     */
    public void finalizar() {
        this.estado.finalizar(this, this.observacoes);
    }

    /**
     * Sobrecarga para finalizar inserindo o diagnóstico/observações médicas.
     */
    public void finalizar(String observacoesClinicas) {
        this.estado.finalizar(this, observacoesClinicas);
    }

    /**
     * Cancela o agendamento da consulta.
     * Conforme diagrama de classes do Astah.
     */
    public void cancelar() {
        this.estado.cancelar(this);
    }

    /**
     * Efetiva o pagamento e quitação da consulta.
     */
    public void pagar(Pagamento pagamento) {
        this.estado.pagar(this, pagamento);
    }

    /**
     * Retorna o valor calculado da consulta para o caixa.
     * Usado no diagrama de sequência SD03.
     */
    public double getValorConsulta() {
        return this.valor;
    }

    public void adicionarExame(Exame exame) {
        if (exame != null && !exames.contains(exame)) {
            exames.add(exame);
            exame.setIdConsultaOrigem(this.idConsulta);
        }
    }

    public void adicionarVacina(Vacina vacina) {
        if (vacina != null && !vacinas.contains(vacina)) {
            vacinas.add(vacina);
            vacina.setIdConsultaOrigem(this.idConsulta);
        }
    }

    // ==========================================
    // Getters e Setters
    // ==========================================

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusConsulta getStatus() {
        return estado.getStatus();
    }

    public void setStatus(StatusConsulta status) {
        this.estado = ConsultaStateFactory.criarEstado(status);
    }

    public void setStatus(String statusStr) {
        this.estado = ConsultaStateFactory.criarEstado(StatusConsulta.fromString(statusStr));
    }

    public ConsultaState getEstadoInterno() {
        return estado;
    }

    public void setEstadoInterno(ConsultaState novoEstado) {
        this.estado = Objects.requireNonNull(novoEstado, "Estado não pode ser nulo");
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
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
        if (!(o instanceof Consulta consulta)) return false;
        return idConsulta == consulta.idConsulta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idConsulta);
    }

    @Override
    public String toString() {
        return "Consulta #" + idConsulta + " - " + (animal != null ? animal.getNome() : "Animal") +
                " com " + (veterinario != null ? veterinario.getNome() : "Veterinário") +
                " [" + getStatus().getDescricao() + "]";
    }
}
