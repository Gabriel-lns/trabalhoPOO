package com.clinicaveterinaria.patterns.strategy;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;

import java.util.UUID;

public class DebitCardPaymentStrategy implements PagamentoStrategy {

    @Override
    public PagamentoResult processar(double valor, Consulta consulta) {
        if (valor <= 0) {
            return PagamentoResult.falha("Valor da transação com Cartão de Débito deve ser positivo.");
        }

        String nsu = "NSU-CD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String nomeAnimal = (consulta != null && consulta.getAnimal() != null) ? consulta.getAnimal().getNome() : "Não especificado";
        String detalhes = "Operadora: Elo/Visa Eletron | Débito em Conta | NSU: " + nsu + " | Paciente: " + nomeAnimal;

        return PagamentoResult.sucesso(nsu, valor, detalhes);
    }

    @Override
    public MetodoPagamento getMetodo() {
        return MetodoPagamento.CARTAO_DEBITO;
    }
}
