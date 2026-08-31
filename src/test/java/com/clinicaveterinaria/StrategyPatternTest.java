package com.clinicaveterinaria;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.patterns.factory.PagamentoStrategyFactory;
import com.clinicaveterinaria.patterns.strategy.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Padrão Strategy (Processamento de Pagamento)")
public class StrategyPatternTest {

    @Test
    @DisplayName("Strategy PIX deve processar com sucesso e gerar código")
    void testPixStrategy() {
        PagamentoStrategy strategy = PagamentoStrategyFactory.obterEstrategia(MetodoPagamento.PIX);
        Consulta consulta = new Consulta(1, LocalDateTime.now(), 150.0, null, null);

        PagamentoResult resultado = strategy.processar(150.0, consulta);

        assertTrue(resultado.sucesso());
        assertTrue(resultado.codigoTransacao().startsWith("PIX-"));
        assertTrue(resultado.comprovante().contains("APROVADO COM SUCESSO"));
        assertEquals(150.0, resultado.valorProcessado());
    }

    @Test
    @DisplayName("Strategy Cartão de Crédito deve gerar NSU")
    void testCreditCardStrategy() {
        PagamentoStrategy strategy = PagamentoStrategyFactory.obterEstrategia(MetodoPagamento.CARTAO_CREDITO);
        Consulta consulta = new Consulta(2, LocalDateTime.now(), 300.0, null, null);

        PagamentoResult resultado = strategy.processar(300.0, consulta);

        assertTrue(resultado.sucesso());
        assertTrue(resultado.codigoTransacao().startsWith("NSU-CC-"));
        assertEquals(300.0, resultado.valorProcessado());
    }

    @Test
    @DisplayName("Strategy Dinheiro deve processar e emitir recibo")
    void testCashStrategy() {
        PagamentoStrategy strategy = PagamentoStrategyFactory.obterEstrategia(MetodoPagamento.DINHEIRO);
        Consulta consulta = new Consulta(3, LocalDateTime.now(), 80.0, null, null);

        PagamentoResult resultado = strategy.processar(80.0, consulta);

        assertTrue(resultado.sucesso());
        assertTrue(resultado.codigoTransacao().startsWith("REC-DIN-"));
    }

    @Test
    @DisplayName("Valor negativo ou zero deve ser rejeitado pela Strategy")
    void testValorInvalido() {
        PagamentoStrategy strategy = PagamentoStrategyFactory.obterEstrategia(MetodoPagamento.PIX);
        Consulta consulta = new Consulta(4, LocalDateTime.now(), 0.0, null, null);

        PagamentoResult resultado = strategy.processar(0.0, consulta);

        assertFalse(resultado.sucesso());
        assertNotNull(resultado.mensagem());
    }
}
