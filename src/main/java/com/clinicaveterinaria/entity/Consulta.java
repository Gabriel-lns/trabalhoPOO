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
 * Entidade de Domínio: Consulta Veterinária.
 * Utiliza o Padrão STATE para gerenciar seu ciclo de vida.
 * Utiliza Static Factory Method para criação consistente.
 */
public class Consulta {
    private int idConsulta;
    private LocalDateTime dataHora;
    private StatusConsulta status;
    private double valor;
    private String observacoes;

    // Relacionamentos do Astah
    private Animal animal;
    private Veterinario veterinario;
    private Pagamento pagamento;
    private final List<Exame> exames;
    private final List<Vacina> vacinas;

    // Referência ao estado atual (State Pattern)
    private ConsultaState estado;

    public Consulta() {
        this.exames = new ArrayList<>();
        this.vacinas = new ArrayList<>();
        this.dataHora = LocalDateTime.now();
        this.valor = 150.0;
        this.status = StatusConsulta.AGENDADA;
        this.estado = new AgendadaState();
        this.observacoes = "";
    }

    public Consulta(int idConsulta, LocalDateTime dataHora, double valor, Animal animal, Veterinario veterinario) {
        this.idConsulta = idConsulta;
        this.dataHora = dataHora != null ? dataHora : LocalDateTime.now();
        this.valor = valor;
        this.animal = animal;
        this.veterinario = veterinario;
        this.status = StatusConsulta.AGENDADA;
        this.estado = new AgendadaState();
        this.observacoes = "";
        this.exames = new ArrayList<>();
        this.vacinas = new ArrayList<>();
    }

    // ==========================================
    // STATIC FACTORY METHODS (Creator Pattern)
    // ==========================================

    /**
     * Cria um novo agendamento consistente no estado Agendada.
     */
    public static Consulta criarAgendamento(Animal animal, Veterinario veterinario, LocalDateTime dataHora, double valor) {
        if (animal == null) {
            throw new IllegalArgumentException("RN04: Animal não pode ser nulo para agendamento.");
        }
        if (veterinario == null) {
            throw new IllegalArgumentException("RN06: Veterinário não pode ser nulo para agendamento.");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("Data e hora da consulta são obrigatórias.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor da consulta deve ser positivo.");
        }

        Consulta c = new Consulta(0, dataHora, valor, animal, veterinario);
        c.setEstadoInterno(new AgendadaState());
        return c;
    }

    /**
     * Reconstrói uma consulta existente vinda da base de dados com seu estado preservado.
     */
    public static Consulta reconstruir(int idConsulta, LocalDateTime dataHora, double valor, StatusConsulta status, String observacoes, Animal animal, Veterinario veterinario) {
        Consulta c = new Consulta(idConsulta, dataHora, valor, animal, veterinario);
        c.setStatus(status);
        c.setObservacoes(observacoes != null ? observacoes : "");
        c.setEstadoInterno(ConsultaStateFactory.criarEstado(status));
        return c;
    }

    // ==========================================
    // MÉTODOS DE NEGÓCIO DELEGADOS AO STATE
    // ==========================================

    public boolean agendar() {
        try {
            this.estado.agendar(this);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public void iniciar() {
        this.estado.iniciar(this);
    }

    public void finalizar(String diagnostico) {
        this.estado.finalizar(this, diagnostico);
    }

    public void cancelar() {
        this.estado.cancelar(this);
    }

    public void pagar(Pagamento pagamento) {
        this.estado.pagar(this, pagamento);
    }

    public double getValorConsulta() {
        return this.valor;
    }

    // Gestão de Coleções
    public void adicionarExame(Exame exame) {
        if (exame != null) this.exames.add(exame);
    }

    public void adicionarVacina(Vacina vacina) {
        if (vacina != null) this.vacinas.add(vacina);
    }

    public List<Exame> getExames() {
        return Collections.unmodifiableList(exames);
    }

    public List<Vacina> getVacinas() {
        return Collections.unmodifiableList(vacinas);
    }

    // Getters e Setters
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
        return status;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
        this.estado = ConsultaStateFactory.criarEstado(status);
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

    public ConsultaState getEstadoInterno() {
        return estado;
    }

    public void setEstadoInterno(ConsultaState estado) {
        this.estado = estado;
        if (estado != null) {
            this.status = estado.getStatus();
        }
    }

    public void setEstado(ConsultaState estado) {
        setEstadoInterno(estado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consulta consulta = (Consulta) o;
        return idConsulta == consulta.idConsulta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idConsulta);
    }

    @Override
    public String toString() {
        return "Consulta #" + idConsulta + " - " + (animal != null ? animal.getNome() : "Sem animal") +
                " [" + status.getDescricao() + "] - R$ " + String.format("%.2f", valor);
    }
}
