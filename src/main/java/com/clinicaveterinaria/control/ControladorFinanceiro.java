package com.clinicaveterinaria.control;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.repository.ConsultaRepository;
import com.clinicaveterinaria.repository.PagamentoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador Financeiro e de Caixa.
 * Mapeado diretamente do diagrama de sequência SD03 - Registrar Pagamento.
 */
public class ControladorFinanceiro {
    private final ConsultaRepository consultaRepository;
    private final PagamentoRepository pagamentoRepository;

    public ControladorFinanceiro() {
        this.consultaRepository = new ConsultaRepository();
        this.pagamentoRepository = new PagamentoRepository();
    }

    public ControladorFinanceiro(ConsultaRepository consultaRepository, PagamentoRepository pagamentoRepository) {
        this.consultaRepository = consultaRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    /**
     * Busca dados para pagamento da consulta realizada (SD03 - Mensagem 2).
     * Valida a Regra de Negócio RN07 (pagamento só permitido após consulta Realizada).
     */
    public Consulta buscarDadosPagamento(int idConsulta) {
        Consulta consulta = consultaRepository.buscarPorId(idConsulta)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + idConsulta));

        if (consulta.getStatus() != StatusConsulta.REALIZADA) {
            throw new IllegalStateException("RN07: O pagamento só pode ser processado para consultas com status 'Realizada'. Status atual: " + consulta.getStatus().getDescricao());
        }

        return consulta;
    }

    /**
     * Processa o pagamento gerando a instância de Pagamento e quitando a consulta (SD03 - Mensagem 7 a 9).
     * Utiliza o Padrão Strategy para execução do pagamento e Padrão State para transição da Consulta.
     */
    public Pagamento processarPagamento(int idConsulta, MetodoPagamento metodo) {
        // 1. Obter e validar consulta
        Consulta consulta = buscarDadosPagamento(idConsulta);
        double valor = consulta.getValorConsulta();

        // 2. Instanciar Pagamento (<<create>> SD03)
        Pagamento pagamento = new Pagamento(0, LocalDate.now(), valor, metodo, idConsulta);

        // 3. Processar via Strategy Pattern
        boolean sucesso = pagamento.processarPagamento(consulta);
        if (!sucesso) {
            throw new RuntimeException("Falha ao processar pagamento com a operadora/método " + metodo.getDescricao());
        }

        // 4. Salvar pagamento no banco SQLite
        pagamento = pagamentoRepository.salvar(pagamento);

        // 5. Transicionar status da consulta para "Paga" via State Pattern
        consulta.pagar(pagamento);
        consultaRepository.atualizar(consulta);

        return pagamento;
    }

    public List<Consulta> listarConsultasPendentesPagamento() {
        return consultaRepository.listarPorStatus(StatusConsulta.REALIZADA);
    }

    public List<Consulta> listarConsultasPagas() {
        return consultaRepository.listarPorStatus(StatusConsulta.PAGA);
    }
}
