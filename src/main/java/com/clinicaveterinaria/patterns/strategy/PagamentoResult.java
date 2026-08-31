package com.clinicaveterinaria.patterns.strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Resultado do processamento de pagamento via Strategy.
 */
public record PagamentoResult(
        boolean sucesso,
        String mensagem,
        String codigoTransacao,
        String comprovante,
        double valorProcessado
) {
    public static PagamentoResult sucesso(String codigoTransacao, double valor, String detalhes) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String recibo = String.format("""
                ========================================
                      RECIBO DE PAGAMENTO CLÍNICA
                ========================================
                Data/Hora : %s
                Transação : %s
                Valor     : R$ %.2f
                Detalhes  : %s
                Status    : APROVADO COM SUCESSO
                ========================================
                """, timestamp, codigoTransacao, valor, detalhes);

        return new PagamentoResult(true, "Pagamento processado com sucesso.", codigoTransacao, recibo, valor);
    }

    public static PagamentoResult falha(String motivo) {
        return new PagamentoResult(false, motivo, null, null, 0.0);
    }
}
