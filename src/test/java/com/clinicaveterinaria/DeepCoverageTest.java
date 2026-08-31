package com.clinicaveterinaria;

import com.clinicaveterinaria.control.ControladorAtendimento;
import com.clinicaveterinaria.control.ControladorCadastros;
import com.clinicaveterinaria.control.ControladorConsulta;
import com.clinicaveterinaria.control.ControladorFinanceiro;
import com.clinicaveterinaria.entity.*;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.patterns.strategy.DebitCardPaymentStrategy;
import com.clinicaveterinaria.patterns.strategy.PagamentoResult;
import com.clinicaveterinaria.patterns.strategy.PagamentoStrategy;
import com.clinicaveterinaria.patterns.strategy.PixPaymentStrategy;
import com.clinicaveterinaria.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Cobertura Profunda (Deep Coverage)")
public class DeepCoverageTest {

    @BeforeAll
    static void initDb() {
        String testDb = "target/test_deep.db";
        new File("target").mkdirs();
        new File(testDb).delete();
        DatabaseManager.setDatabasePath(testDb);
        DatabaseManager.inicializarBanco();
    }

    @Test
    @DisplayName("DatabaseManager: conexão ativa e re-execução do seeder")
    void testDatabaseManager() throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());

            // Reexecução do SeedDatabase quando já populado não deve gerar erro
            SeedDatabase.executar(conn);
        }
    }

    @Test
    @DisplayName("Repositórios: buscas por inexistentes e listagens vazias")
    void testRepositoriosInexistentes() {
        TutorRepository tutorRepo = new TutorRepository();
        VeterinarioRepository vetRepo = new VeterinarioRepository();
        AnimalRepository animalRepo = new AnimalRepository();
        ConsultaRepository consultaRepo = new ConsultaRepository();
        PagamentoRepository pagRepo = new PagamentoRepository();

        assertTrue(tutorRepo.buscarPorCpf("CPF-INEXISTENTE-000").isEmpty());
        assertTrue(vetRepo.buscarPorCrmv("CRMV-INEXISTENTE-000").isEmpty());
        assertTrue(animalRepo.buscarPorId(999999).isEmpty());
        assertTrue(consultaRepo.buscarPorId(999999).isEmpty());
        assertTrue(pagRepo.buscarPorConsultaId(999999).isEmpty());

        List<Consulta> canceladas = consultaRepo.listarPorStatus(StatusConsulta.CANCELADA);
        assertNotNull(canceladas);
    }

    @Test
    @DisplayName("ControladorAtendimento: listagem de consultas ativas e prontuário")
    void testControladorAtendimentoListas() {
        ControladorAtendimento ctrlA = new ControladorAtendimento();
        List<Consulta> ativas = ctrlA.listarConsultasParaAtendimento();
        assertNotNull(ativas);

        List<Exame> exames = ctrlA.listarExamesDoProntuario(1);
        assertNotNull(exames);

        List<Vacina> vacinas = ctrlA.listarVacinasDoProntuario(1);
        assertNotNull(vacinas);
    }

    @Test
    @DisplayName("Controladores: injeção de dependência via construtores parametrizados")
    void testControladoresConstrutoresParametrizados() {
        ConsultaRepository cRepo = new ConsultaRepository();
        AnimalRepository aRepo = new AnimalRepository();
        VeterinarioRepository vRepo = new VeterinarioRepository();
        ProntuarioRepository pRepo = new ProntuarioRepository();
        ExameRepository eRepo = new ExameRepository();
        VacinaRepository vacRepo = new VacinaRepository();
        PagamentoRepository pagRepo = new PagamentoRepository();

        ControladorConsulta ctrlC = new ControladorConsulta(cRepo, aRepo, vRepo);
        ControladorAtendimento ctrlA = new ControladorAtendimento(cRepo, pRepo, eRepo, vacRepo);
        ControladorFinanceiro ctrlF = new ControladorFinanceiro(cRepo, pagRepo);

        assertNotNull(ctrlC.listarTodasConsultas());
        assertNotNull(ctrlA.listarConsultasParaAtendimento());
        assertNotNull(ctrlF.listarConsultasPendentesPagamento());
    }

    @Test
    @DisplayName("Strategy e Result: métodos de getters")
    void testStrategyGetters() {
        PagamentoStrategy pix = new PixPaymentStrategy();
        assertEquals(MetodoPagamento.PIX, pix.getMetodo());

        PagamentoStrategy debito = new DebitCardPaymentStrategy();
        assertEquals(MetodoPagamento.CARTAO_DEBITO, debito.getMetodo());

        // Testar débito com valor inválido
        PagamentoResult falhaDebito = debito.processar(-10.0, new Consulta());
        assertFalse(falhaDebito.sucesso());
    }
}
