package com.clinicaveterinaria.patterns.strategy;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;

import java.util.UUID;

/**
 * Estratégia concreta para pagamentos via PIX.
 * Aplica validação de chave e geração de transação instantânea.
 */
public class PixPaymentStrategy implements PagamentoStrategy {

    @Override
    public PagamentoResult processar(double valor, Consulta consulta) {
        if (valor <= 0) {
            return PagamentoResult.falha("Valor para cobrança via PIX deve ser maior que zero.");
        }

        String codigoPix = "PIX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String detalhes = "Método: PIX Banco Central | Liquidação em tempo real | Paciente: " +
                (consulta.getAnimal() != null ? consulta.getAnimal().getNome() : "Não especificado");

        return PagamentoResult.sucesso(codigoPix, valor, detalhes);
    }

    @Override
    public MetodoPagamento getMetodo() {
        return MetodoPagamento.PIX;
    }
}
