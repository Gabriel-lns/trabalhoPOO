package com.clinicaveterinaria;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Padrão State (Ciclo de Vida da Consulta)")
public class StatePatternTest {

    private Consulta consulta;

    @BeforeEach
    void setUp() {
        consulta = new Consulta(1, LocalDateTime.now(), 200.0, null, null);
    }

    @Test
    @DisplayName("Consulta recém-criada deve iniciar no estado Agendada")
    void testEstadoInicial() {
        assertEquals(StatusConsulta.AGENDADA, consulta.getStatus());
    }

    @Test
    @DisplayName("Transição de Agendada -> EmAndamento -> Realizada -> Paga")
    void testFluxoCompletoValido() {
        // Iniciar
        consulta.iniciar();
        assertEquals(StatusConsulta.EM_ANDAMENTO, consulta.getStatus());

        // Finalizar
        consulta.finalizar("Exame clínico sem alterações.");
        assertEquals(StatusConsulta.REALIZADA, consulta.getStatus());
        assertEquals("Exame clínico sem alterações.", consulta.getObservacoes());

        // Pagar
        Pagamento pagamento = new Pagamento(1, LocalDate.now(), 200.0, MetodoPagamento.PIX, 1);
        consulta.pagar(pagamento);
        assertEquals(StatusConsulta.PAGA, consulta.getStatus());
        assertNotNull(consulta.getPagamento());
    }

    @Test
    @DisplayName("RN07: Bloqueio de pagamento para consulta não realizada")
    void testBloqueioPagamentoConsultaAgendada() {
        Pagamento pagamento = new Pagamento(1, LocalDate.now(), 200.0, MetodoPagamento.PIX, 1);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> consulta.pagar(pagamento));
        assertTrue(ex.getMessage().contains("RN07"));
    }

    @Test
    @DisplayName("Cancelamento de consulta agendada")
    void testCancelamento() {
        consulta.cancelar();
        assertEquals(StatusConsulta.CANCELADA, consulta.getStatus());

        // Tentar iniciar consulta cancelada deve falhar
        assertThrows(IllegalStateException.class, () -> consulta.iniciar());
    }

    @Test
    @DisplayName("Consulta já realizada não pode ser cancelada")
    void testBloqueioCancelamentoConsultaRealizada() {
        consulta.iniciar();
        consulta.finalizar("OK");

        assertThrows(IllegalStateException.class, () -> consulta.cancelar());
    }
}
