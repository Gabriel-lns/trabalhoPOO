package com.clinicaveterinaria.patterns.strategy;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;

import java.util.UUID;

/**
 * Estratégia concreta para pagamentos via Cartão de Crédito.
 */
public class CreditCardPaymentStrategy implements PagamentoStrategy {

    @Override
    public PagamentoResult processar(double valor, Consulta consulta) {
        if (valor <= 0) {
            return PagamentoResult.falha("Valor para cobrança via Cartão de Crédito deve ser maior que zero.");
        }

        String nsu = "NSU-CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String detalhes = "Método: Cartão de Crédito (Gateway Adquirente) | Transação Autorizada";

        return PagamentoResult.sucesso(nsu, valor, detalhes);
    }

    @Override
    public MetodoPagamento getMetodo() {
        return MetodoPagamento.CARTAO_CREDITO;
    }
}
