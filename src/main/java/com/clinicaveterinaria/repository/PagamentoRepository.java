package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Pagamento;
import java.util.Optional;

/**
 * Interface / Contrato de Persistência para Pagamentos (DIP).
 */
public interface PagamentoRepository {
    Pagamento salvar(Pagamento pagamento);
    Optional<Pagamento> buscarPorId(int idPagamento);
    Optional<Pagamento> buscarPorConsultaId(int idConsulta);
}
