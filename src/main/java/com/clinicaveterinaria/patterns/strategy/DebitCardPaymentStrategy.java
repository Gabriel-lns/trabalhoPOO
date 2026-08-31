package com.clinicaveterinaria.patterns.strategy;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;

import java.util.UUID;

/**
 * Estratégia concreta para pagamentos via Cartão de Débito.
 */
public class DebitCardPaymentStrategy implements PagamentoStrategy {

    @Override
    public PagamentoResult processar(double valor, Consulta consulta) {
        if (valor <= 0) {
            return PagamentoResult.falha("Valor para cobrança via Cartão de Débito deve ser maior que zero.");
        }

        String nsu = "NSU-CD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String detalhes = "Método: Cartão de Débito | Débito em Conta Autorizado";

        return PagamentoResult.sucesso(nsu, valor, detalhes);
    }

    @Override
    public MetodoPagamento getMetodo() {
        return MetodoPagamento.CARTAO_DEBITO;
    }
}
