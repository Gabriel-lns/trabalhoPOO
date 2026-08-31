package com.clinicaveterinaria.patterns.factory;

import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.patterns.strategy.*;

/**
 * Padrão de Projeto Criacional FACTORY METHOD (GoF).
 * Centraliza a criação e resolução das estratégias de pagamento.
 */
public class PagamentoStrategyFactory {

    public static PagamentoStrategy obterEstrategia(MetodoPagamento metodo) {
        if (metodo == null) {
            return new PixPaymentStrategy();
        }
        return switch (metodo) {
            case PIX -> new PixPaymentStrategy();
            case CARTAO_CREDITO -> new CreditCardPaymentStrategy();
            case CARTAO_DEBITO -> new DebitCardPaymentStrategy();
            case DINHEIRO -> new CashPaymentStrategy();
        };
    }
}
