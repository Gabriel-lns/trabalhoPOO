package com.clinicaveterinaria.patterns.strategy;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;

/**
 * Interface do Padrão de Projeto Comportamental STRATEGY (GoF).
 * Permite que diferentes algoritmos de pagamento sejam executados de forma intercambiável.
 */
public interface PagamentoStrategy {
    PagamentoResult processar(double valor, Consulta consulta);
    MetodoPagamento getMetodo();
}
