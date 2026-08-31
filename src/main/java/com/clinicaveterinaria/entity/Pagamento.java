package com.clinicaveterinaria.entity;

import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.patterns.factory.PagamentoStrategyFactory;
import com.clinicaveterinaria.patterns.strategy.PagamentoResult;
import com.clinicaveterinaria.patterns.strategy.PagamentoStrategy;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade Pagamento conforme modelado no Astah.
 * Representa a liquidação financeira de uma Consulta realizada.
 */
public class Pagamento {
    private int idPagamento;
    private LocalDate dataPagamento;
    private double valorPago;
    private MetodoPagamento metodoPagamento;
    private int idConsulta;
    private String codigoTransacao;
    private String comprovante;
    private boolean processadoComSucesso;

    public Pagamento() {
        this.dataPagamento = LocalDate.now();
    }

    public Pagamento(int idPagamento, LocalDate dataPagamento, double valorPago, MetodoPagamento metodoPagamento, int idConsulta) {
        this.idPagamento = idPagamento;
        this.dataPagamento = dataPagamento != null ? dataPagamento : LocalDate.now();
        this.valorPago = valorPago;
        this.metodoPagamento = metodoPagamento != null ? metodoPagamento : MetodoPagamento.PIX;
        this.idConsulta = idConsulta;
    }

    /**
     * Processa o pagamento utilizando o padrão Strategy.
     * Conforme diagrama de classes do Astah.
     */
    public boolean processarPagamento(Consulta consulta) {
        PagamentoStrategy strategy = PagamentoStrategyFactory.obterEstrategia(this.metodoPagamento);
        PagamentoResult resultado = strategy.processar(this.valorPago, consulta);
        
        if (resultado.sucesso()) {
            this.codigoTransacao = resultado.codigoTransacao();
            this.comprovante = resultado.comprovante();
            this.processadoComSucesso = true;
            return true;
        } else {
            this.processadoComSucesso = false;
            return false;
        }
    }

    /**
     * Sobrecarga sem parâmetros para compatibilidade estrita com a assinatura original do Astah.
     */
    public boolean processarPagamento() {
        PagamentoStrategy strategy = PagamentoStrategyFactory.obterEstrategia(this.metodoPagamento);
        PagamentoResult resultado = strategy.processar(this.valorPago, new Consulta());
        if (resultado.sucesso()) {
            this.codigoTransacao = resultado.codigoTransacao();
            this.comprovante = resultado.comprovante();
            this.processadoComSucesso = true;
            return true;
        }
        return false;
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

    public boolean isProcessadoComSucesso() {
        return processadoComSucesso;
    }

    public void setProcessadoComSucesso(boolean processadoComSucesso) {
        this.processadoComSucesso = processadoComSucesso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pagamento pagamento)) return false;
        return idPagamento == pagamento.idPagamento;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPagamento);
    }

    @Override
    public String toString() {
        return "Pagamento #" + idPagamento + " - R$ " + String.format("%.2f", valorPago) + " (" + metodoPagamento + ")";
    }
}
