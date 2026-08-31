package com.clinicaveterinaria;

import com.clinicaveterinaria.control.ControladorAtendimento;
import com.clinicaveterinaria.control.ControladorCadastros;
import com.clinicaveterinaria.control.ControladorConsulta;
import com.clinicaveterinaria.control.ControladorFinanceiro;
import com.clinicaveterinaria.entity.*;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.repository.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Integração dos Controladores (BCE Layer)")
public class ControladoresIntegrationTest {

    private static ControladorConsulta ctrlConsulta;
    private static ControladorAtendimento ctrlAtendimento;
    private static ControladorFinanceiro ctrlFinanceiro;
    private static ControladorCadastros ctrlCadastros;

    @BeforeAll
    static void setupDatabase() {
        String testDb = "target/test_controladores.db";
        new File("target").mkdirs();
        new File(testDb).delete();
        DatabaseManager.setDatabasePath(testDb);
        DatabaseManager.inicializarBanco();

        ctrlConsulta = new ControladorConsulta();
        ctrlAtendimento = new ControladorAtendimento();
        ctrlFinanceiro = new ControladorFinanceiro();
        ctrlCadastros = new ControladorCadastros();
    }

    @Test
    @DisplayName("Fluxo Completo de Atendimento Clínico e Faturamento (SD01 -> SD02 -> SD03)")
    void testFluxoCompletoClinica() {
        // 1. Cadastrar Tutor, Veterinário e Animal
        Tutor tutor = ctrlCadastros.cadastrarTutor("111.000.111-22", "Marcos Paulo", "(21) 98888-0000", "Rua A, 10");
        Veterinario vet = ctrlCadastros.cadastrarVeterinario("CRMV-FLUXO-99", "Dr. Claudio", "Cardiologia", "(21) 97777-1111");
        Animal animal = ctrlCadastros.cadastrarAnimal("Max", "Canina", "Labrador", LocalDate.now().minusYears(2), tutor.getCpf());

        assertNotNull(tutor);
        assertNotNull(vet);
        assertNotNull(animal);
        assertTrue(animal.getIdAnimal() > 0);

        // 2. Agendar Consulta (SD01)
        LocalDateTime horario = LocalDateTime.now().plusDays(10).withHour(15).withMinute(0);
        Consulta consulta = ctrlConsulta.solicitarAgendamento(vet.getCrmv(), animal.getIdAnimal(), horario, 220.0);

        assertNotNull(consulta);
        assertEquals(StatusConsulta.AGENDADA, consulta.getStatus());
        assertEquals(220.0, consulta.getValor());

        // 3. Iniciar Atendimento Clínico (SD02)
        Consulta emAtendimento = ctrlAtendimento.iniciarAtendimento(consulta.getIdConsulta());
        assertEquals(StatusConsulta.EM_ANDAMENTO, emAtendimento.getStatus());

        // 4. Buscar Histórico do Prontuário
        Prontuario prontuario = ctrlAtendimento.buscarHistoricoAnimal(animal.getIdAnimal());
        assertNotNull(prontuario);

        // 5. Registrar Exame e Vacina no Atendimento
        Exame exame = ctrlAtendimento.registrarExame(consulta.getIdConsulta(), prontuario.getIdProntuario(), "Ecocardiograma", "Normal");
        Vacina vacina = ctrlAtendimento.registrarVacina(consulta.getIdConsulta(), prontuario.getIdProntuario(), "Giardia", LocalDate.now().plusYears(1));

        assertNotNull(exame);
        assertNotNull(vacina);

        // 6. Finalizar Atendimento e Gravar Evolução no Prontuário (SD02)
        ctrlAtendimento.finalizarConsulta(consulta.getIdConsulta(), "Paciente com ritmo cardíaco regular, peso 28kg.");

        // Validar que o prontuário agora possui a evolução
        Prontuario prontuarioAtualizado = ctrlAtendimento.buscarHistoricoAnimal(animal.getIdAnimal());
        boolean encontrouRegistro = prontuarioAtualizado.consultarHistorico().stream()
                .anyMatch(r -> r.contains("ritmo cardíaco regular"));
        assertTrue(encontrouRegistro, "Evolução clínica deve estar gravada no prontuário");

        // 7. Faturamento no Caixa (SD03)
        Consulta consultaRealizada = ctrlFinanceiro.buscarDadosPagamento(consulta.getIdConsulta());
        assertEquals(StatusConsulta.REALIZADA, consultaRealizada.getStatus());

        Pagamento pagamento = ctrlFinanceiro.processarPagamento(consulta.getIdConsulta(), MetodoPagamento.PIX);
        assertNotNull(pagamento);
        assertTrue(pagamento.isProcessadoComSucesso());
        assertTrue(pagamento.getComprovante().contains("APROVADO"));

        // Validar que a consulta transicionou para Paga
        List<Consulta> pagas = ctrlFinanceiro.listarConsultasPagas();
        boolean estaPaga = pagas.stream().anyMatch(c -> c.getIdConsulta() == consulta.getIdConsulta());
        assertTrue(estaPaga, "A consulta deve constar na lista de consultas pagas");
    }

    @Test
    @DisplayName("Validação de Regras de Negócio no Controlador de Cadastros")
    void testValidacoesCadastros() {
        // Validação de campos vazios
        assertThrows(IllegalArgumentException.class, () -> ctrlCadastros.cadastrarTutor("", "", "", ""));
        assertThrows(IllegalArgumentException.class, () -> ctrlCadastros.cadastrarVeterinario("", "", "", ""));
        assertThrows(IllegalArgumentException.class, () -> ctrlCadastros.cadastrarAnimal("", "Canina", "SRD", LocalDate.now(), "999"));

        // RN01: Animal com tutor inexistente deve falhar
        assertThrows(IllegalArgumentException.class, () ->
                ctrlCadastros.cadastrarAnimal("Totó", "Canina", "SRD", LocalDate.now(), "CPF-INEXISTENTE-999")
        );
    }
}
