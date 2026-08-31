package com.clinicaveterinaria.patterns.strategy;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;

import java.util.UUID;

public class CashPaymentStrategy implements PagamentoStrategy {

    @Override
    public PagamentoResult processar(double valor, Consulta consulta) {
        if (valor <= 0) {
            return PagamentoResult.falha("Valor recebido em dinheiro deve ser maior que zero.");
        }

        String recibo = "REC-DIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String nomeAnimal = (consulta != null && consulta.getAnimal() != null) ? consulta.getAnimal().getNome() : "Não especificado";
        String detalhes = "Recebimento em Espécie no Balcão | Recibo: " + recibo + " | Paciente: " + nomeAnimal;

        return PagamentoResult.sucesso(recibo, valor, detalhes);
    }

    @Override
    public MetodoPagamento getMetodo() {
        return MetodoPagamento.DINHEIRO;
    }
}
