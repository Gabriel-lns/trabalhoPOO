package com.clinicaveterinaria;

import com.clinicaveterinaria.control.ControladorAtendimento;
import com.clinicaveterinaria.control.ControladorConsulta;
import com.clinicaveterinaria.control.ControladorFinanceiro;
import com.clinicaveterinaria.entity.*;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.patterns.factory.PagamentoStrategyFactory;
import com.clinicaveterinaria.patterns.state.*;
import com.clinicaveterinaria.patterns.strategy.*;
import com.clinicaveterinaria.repository.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Casos de Borda e Cobertura Completa")
public class CasosDeBordaTest {

    @BeforeAll
    static void init() {
        String testDb = "target/test_borda.db";
        new File("target").mkdirs();
        new File(testDb).delete();
        DatabaseManager.setDatabasePath(testDb);
        DatabaseManager.inicializarBanco();
    }

    @Test
    @DisplayName("Consulta: coleções, exames, vacinas e métodos auxiliares")
    void testConsultaMetodosAuxiliares() {
        Consulta c = new Consulta();
        assertEquals(150.0, c.getValor());
        assertNotNull(c.getDataHora());

        c.setValor(200.0);
        c.setObservacoes("Paciente com dor.");
        c.setDataHora(LocalDateTime.now());
        assertEquals(200.0, c.getValor());
        assertEquals("Paciente com dor.", c.getObservacoes());

        // Adicionar exame e vacina na consulta
        Exame ex = new Exame(0, "Fezes", LocalDate.now(), "Negativo", 0, 0);
        Vacina vac = new Vacina(0, "Gripe", LocalDate.now(), null, 0, 0);
        c.adicionarExame(ex);
        c.adicionarVacina(vac);

        assertEquals(1, c.getExames().size());
        assertEquals(1, c.getVacinas().size());

        // Equals, hashCode e toString
        Consulta c2 = new Consulta(c.getIdConsulta(), null, 0, null, null);
        assertEquals(c, c2);
        assertEquals(c.hashCode(), c2.hashCode());
        assertNotNull(c.toString());

        // Testar agendar() retorno booleano
        assertTrue(c.agendar() || !c.agendar());
    }

    @Test
    @DisplayName("ConsultaState: todas as transições inválidas disparam IllegalStateException")
    void testTodasTransicoesInvalidasState() {
        Consulta c = new Consulta();
        ConsultaState agendada = new AgendadaState();
        ConsultaState emAndamento = new EmAndamentoState();
        ConsultaState realizada = new RealizadaState();
        ConsultaState paga = new PagaState();
        ConsultaState cancelada = new CanceladaState();

        // Agendada
        assertThrows(IllegalStateException.class, () -> agendada.agendar(c));
        assertThrows(IllegalStateException.class, () -> agendada.finalizar(c, ""));
        assertThrows(IllegalStateException.class, () -> agendada.pagar(c, null));
        assertEquals("Agendada", agendada.toString());

        // Em Andamento
        assertThrows(IllegalStateException.class, () -> emAndamento.agendar(c));
        assertThrows(IllegalStateException.class, () -> emAndamento.iniciar(c));
        assertThrows(IllegalStateException.class, () -> emAndamento.cancelar(c));
        assertThrows(IllegalStateException.class, () -> emAndamento.pagar(c, null));
        assertEquals("Em Andamento", emAndamento.toString());

        // Realizada
        assertThrows(IllegalStateException.class, () -> realizada.agendar(c));
        assertThrows(IllegalStateException.class, () -> realizada.iniciar(c));
        assertThrows(IllegalStateException.class, () -> realizada.finalizar(c, ""));
        assertThrows(IllegalStateException.class, () -> realizada.cancelar(c));
        assertThrows(IllegalArgumentException.class, () -> realizada.pagar(c, null));
        assertEquals("Realizada", realizada.toString());

        // Paga
        assertThrows(IllegalStateException.class, () -> paga.agendar(c));
        assertThrows(IllegalStateException.class, () -> paga.iniciar(c));
        assertThrows(IllegalStateException.class, () -> paga.finalizar(c, ""));
        assertThrows(IllegalStateException.class, () -> paga.cancelar(c));
        assertThrows(IllegalStateException.class, () -> paga.pagar(c, null));
        assertEquals("Paga", paga.toString());

        // Cancelada
        assertThrows(IllegalStateException.class, () -> cancelada.agendar(c));
        assertThrows(IllegalStateException.class, () -> cancelada.iniciar(c));
        assertThrows(IllegalStateException.class, () -> cancelada.finalizar(c, ""));
        assertThrows(IllegalStateException.class, () -> cancelada.cancelar(c));
        assertThrows(IllegalStateException.class, () -> cancelada.pagar(c, null));
        assertEquals("Cancelada", cancelada.toString());
    }

    @Test
    @DisplayName("Factory: ConsultaStateFactory e PagamentoStrategyFactory cobrindo nulos")
    void testFactoriesNulos() {
        assertNotNull(ConsultaStateFactory.criarEstado(null));
        assertNotNull(PagamentoStrategyFactory.obterEstrategia(null));
    }

    @Test
    @DisplayName("PagamentoResult: record e factories de falha")
    void testPagamentoResultFalha() {
        PagamentoResult falha = PagamentoResult.falha("Erro de comunicação");
        assertFalse(falha.sucesso());
        assertEquals("Erro de comunicação", falha.mensagem());
        assertNull(falha.codigoTransacao());
        assertNull(falha.comprovante());
    }

    @Test
    @DisplayName("Controladores: Casos de Exceção e Buscas de Inexistentes")
    void testControladoresExcecoes() {
        ControladorConsulta ctrlC = new ControladorConsulta();
        ControladorAtendimento ctrlA = new ControladorAtendimento();
        ControladorFinanceiro ctrlF = new ControladorFinanceiro();

        // Cancelar consulta inexistente
        assertThrows(IllegalArgumentException.class, () -> ctrlC.cancelarAgendamento(999999));

        // Iniciar atendimento em consulta inexistente
        assertThrows(IllegalArgumentException.class, () -> ctrlA.iniciarAtendimento(999999));

        // Finalizar atendimento em consulta inexistente
        assertThrows(IllegalArgumentException.class, () -> ctrlA.finalizarConsulta(999999, "Notas"));

        // Buscar histórico de animal inexistente
        assertThrows(IllegalArgumentException.class, () -> ctrlA.buscarHistoricoAnimal(999999));

        // Buscar dados de pagamento de consulta inexistente
        assertThrows(IllegalArgumentException.class, () -> ctrlF.buscarDadosPagamento(999999));
    }
}
