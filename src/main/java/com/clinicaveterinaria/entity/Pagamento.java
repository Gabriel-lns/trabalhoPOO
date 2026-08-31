package com.clinicaveterinaria.entity;

import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.patterns.factory.PagamentoStrategyFactory;
import com.clinicaveterinaria.patterns.strategy.PagamentoResult;
import com.clinicaveterinaria.patterns.strategy.PagamentoStrategy;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade de Domínio: Pagamento de Consulta.
 * Utiliza o Padrão STRATEGY para executar o algoritmo correspondente.
 * Utiliza Static Factory Method para instanciação controlada.
 */
public class Pagamento {
    private int idPagamento;
    private LocalDate dataPagamento;
    private double valorPago;
    private MetodoPagamento metodoPagamento;
    private int idConsulta;
    private String codigoTransacao;
    private String comprovante;

    // Estratégia de Pagamento (Strategy Pattern)
    private PagamentoStrategy strategy;
    private boolean processadoComSucesso;

    public Pagamento() {
        this.dataPagamento = LocalDate.now();
        this.metodoPagamento = MetodoPagamento.PIX;
        this.strategy = PagamentoStrategyFactory.obterEstrategia(this.metodoPagamento);
    }

    public Pagamento(int idPagamento, LocalDate dataPagamento, double valorPago, MetodoPagamento metodoPagamento, int idConsulta) {
        this.idPagamento = idPagamento;
        this.dataPagamento = dataPagamento != null ? dataPagamento : LocalDate.now();
        this.valorPago = valorPago;
        this.metodoPagamento = metodoPagamento != null ? metodoPagamento : MetodoPagamento.PIX;
        this.idConsulta = idConsulta;
        this.strategy = PagamentoStrategyFactory.obterEstrategia(this.metodoPagamento);
    }

    // ==========================================
    // STATIC FACTORY METHODS (Creator Pattern)
    // ==========================================

    /**
     * Cria um novo objeto de pagamento consistente para processamento.
     */
    public static Pagamento criarPagamento(int idConsulta, double valor, MetodoPagamento metodo) {
        if (idConsulta <= 0) {
            throw new IllegalArgumentException("ID da consulta inválido para pagamento.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser positivo.");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("Método de pagamento é obrigatório.");
        }

        return new Pagamento(0, LocalDate.now(), valor, metodo, idConsulta);
    }

    /**
     * Reconstrói um pagamento existente da base de dados.
     */
    public static Pagamento reconstruir(int idPagamento, LocalDate dataPagamento, double valorPago, MetodoPagamento metodoPagamento, int idConsulta, String codigoTransacao, String comprovante) {
        Pagamento p = new Pagamento(idPagamento, dataPagamento, valorPago, metodoPagamento, idConsulta);
        p.setCodigoTransacao(codigoTransacao);
        p.setComprovante(comprovante);
        p.setProcessadoComSucesso(true);
        return p;
    }

    // ==========================================
    // MÉTODO DE PROCESSAMENTO VIA STRATEGY
    // ==========================================

    public boolean processarPagamento(Consulta consulta) {
        if (strategy == null) {
            this.strategy = PagamentoStrategyFactory.obterEstrategia(this.metodoPagamento);
        }

        PagamentoResult resultado = strategy.processar(this.valorPago, consulta);
        this.processadoComSucesso = resultado.sucesso();

        if (resultado.sucesso()) {
            this.codigoTransacao = resultado.codigoTransacao();
            this.comprovante = resultado.comprovante();
        }

        return this.processadoComSucesso;
    }

    public boolean processarPagamento() {
        return processarPagamento(null);
    }

    // Getters e Setters
    public int getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(int idPagamento) {
        this.idPagamento = idPagamento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
        this.strategy = PagamentoStrategyFactory.obterEstrategia(metodoPagamento);
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public void setCodigoTransacao(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }

    public String getComprovante() {
        return comprovante;
    }

    public void setComprovante(String comprovante) {
        this.comprovante = comprovante;
    }

    public PagamentoStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(PagamentoStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean isProcessadoComSucesso() {
        return processadoComSucesso;
    }

    public void setProcessadoComSucesso(boolean processadoComSucesso) {
        this.processadoComSucesso = processadoComSucesso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return idPagamento == pagamento.idPagamento;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPagamento);
    }

    @Override
    public String toString() {
        return "Pagamento #" + idPagamento + " - " + metodoPagamento.getDescricao() +
                " - R$ " + String.format("%.2f", valorPago) + " (" + (processadoComSucesso ? "Confirmado" : "Pendente") + ")";
    }
}
