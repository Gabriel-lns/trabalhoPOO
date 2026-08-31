package com.clinicaveterinaria.patterns.strategy;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;

import java.util.UUID;

/**
 * Estratégia concreta para pagamentos em Dinheiro em Espécie.
 */
public class CashPaymentStrategy implements PagamentoStrategy {

    @Override
    public PagamentoResult processar(double valor, Consulta consulta) {
        if (valor <= 0) {
            return PagamentoResult.falha("Valor para recebimento em espécie deve ser maior que zero.");
        }

        String reciboId = "REC-DIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String detalhes = "Método: Dinheiro em Espécie | Conferido no Caixa da Recepção";

        return PagamentoResult.sucesso(reciboId, valor, detalhes);
    }

    @Override
    public MetodoPagamento getMetodo() {
        return MetodoPagamento.DINHEIRO;
    }
}
