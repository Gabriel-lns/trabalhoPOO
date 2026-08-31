package com.clinicaveterinaria;

import com.clinicaveterinaria.entity.*;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Integração Direta com a Camada de Repositórios (SQLite)")
public class RepositoriosIntegrationTest {

    private static TutorRepository tutorRepo;
    private static VeterinarioRepository vetRepo;
    private static AnimalRepository animalRepo;
    private static ProntuarioRepository prontuarioRepo;
    private static ExameRepository exameRepo;
    private static VacinaRepository vacinaRepo;
    private static ConsultaRepository consultaRepo;
    private static PagamentoRepository pagamentoRepo;

    @BeforeAll
    static void init() {
        String testDb = "target/test_repositorios.db";
        new File("target").mkdirs();
        new File(testDb).delete();
        DatabaseManager.setDatabasePath(testDb);
        DatabaseManager.inicializarBanco();

        RepositoryFactory factory = RepositoryFactory.getInstance();
        tutorRepo = factory.getTutorRepository();
        vetRepo = factory.getVeterinarioRepository();
        animalRepo = factory.getAnimalRepository();
        prontuarioRepo = factory.getProntuarioRepository();
        exameRepo = factory.getExameRepository();
        vacinaRepo = factory.getVacinaRepository();
        consultaRepo = factory.getConsultaRepository();
        pagamentoRepo = factory.getPagamentoRepository();
    }

    @Test
    @DisplayName("CRUD Tutor e Veterinario no SQLite")
    void testTutorEVeterinario() {
        Tutor tutor = new Tutor("333.444.555-66", "Julio Cesar", "22999991111", "Rua das Acacias");
        tutorRepo.salvar(tutor);

        Optional<Tutor> buscaTutor = tutorRepo.buscarPorCpf("333.444.555-66");
        assertTrue(buscaTutor.isPresent());
        assertEquals("Julio Cesar", buscaTutor.get().getNome());

        Veterinario vet = new Veterinario("CRMV-REPO-01", "Dra. Renata", "Anestesiologia", "22988882222");
        vetRepo.salvar(vet);

        Optional<Veterinario> buscaVet = vetRepo.buscarPorCrmv("CRMV-REPO-01");
        assertTrue(buscaVet.isPresent());
        assertEquals("Dra. Renata", buscaVet.get().getNome());
    }

    @Test
    @DisplayName("Persistência e recuperação de Animal e Prontuário")
    void testAnimalEProntuario() {
        Tutor tutor = new Tutor("888.777.666-55", "Luciana", "22997771122", "Av. Brasil");
        tutorRepo.salvar(tutor);

        Animal animal = new Animal(0, "Pipoca", "Felina", "Angorá", LocalDate.now().minusYears(1), tutor);
        animal = animalRepo.salvar(animal);

        assertTrue(animal.getIdAnimal() > 0);

        Optional<Animal> buscaAnimal = animalRepo.buscarPorId(animal.getIdAnimal());
        assertTrue(buscaAnimal.isPresent());
        assertEquals("Pipoca", buscaAnimal.get().getNome());

        // Prontuário automático
        Optional<Prontuario> prontuario = prontuarioRepo.buscarPorAnimalId(animal.getIdAnimal(), animal);
        assertTrue(prontuario.isPresent());

        // Adicionar registros ao prontuário
        prontuarioRepo.adicionarRegistroClinico(prontuario.get().getIdProntuario(), "Vacinação em dia.");
        Optional<Prontuario> prontuarioComRegistro = prontuarioRepo.buscarPorAnimalId(animal.getIdAnimal(), animal);
        assertTrue(prontuarioComRegistro.get().consultarHistorico().stream().anyMatch(r -> r.contains("Vacinação em dia.")));
    }

    @Test
    @DisplayName("Persistência e recuperação de Exames e Vacinas")
    void testExamesEVacinas() {
        Exame exame = new Exame(0, "Ultrassom Abdominal", LocalDate.now(), "Estruturas preservadas", 1, 1);
        exame = exameRepo.salvar(exame);
        assertTrue(exame.getIdExame() > 0);

        List<Exame> listaExames = exameRepo.listarPorProntuario(1);
        assertFalse(listaExames.isEmpty());

        Vacina vacina = new Vacina(0, "Gripe Canina", LocalDate.now(), LocalDate.now().plusYears(1), 1, 1);
        vacina = vacinaRepo.salvar(vacina);
        assertTrue(vacina.getIdVacina() > 0);

        List<Vacina> listaVacinas = vacinaRepo.listarPorProntuario(1);
        assertFalse(listaVacinas.isEmpty());
    }

    @Test
    @DisplayName("Persistência e ciclo de Consulta e Pagamento no SQLite")
    void testConsultaEPagamento() {
        Tutor tutor = new Tutor("777.888.999-00", "Renato", "2299", "Rua Y");
        tutorRepo.salvar(tutor);
        Veterinario vet = new Veterinario("CRMV-REPO-02", "Dr. Andre", "Cirurgia", "2298");
        vetRepo.salvar(vet);
        Animal animal = animalRepo.salvar(new Animal(0, "Duke", "Canina", "Rottweiler", LocalDate.now().minusYears(4), tutor));

        Consulta consulta = new Consulta(0, LocalDateTime.now(), 200.0, animal, vet);
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta = consultaRepo.salvar(consulta);

        assertTrue(consulta.getIdConsulta() > 0);

        // Atualizar para Realizada
        consulta.setStatus(StatusConsulta.REALIZADA);
        consulta.setObservacoes("Procedimento cirúrgico concluído.");
        consultaRepo.atualizar(consulta);

        Optional<Consulta> busca = consultaRepo.buscarPorId(consulta.getIdConsulta());
        assertTrue(busca.isPresent());
        assertEquals(StatusConsulta.REALIZADA, busca.get().getStatus());

        // Salvar Pagamento
        Pagamento pagamento = new Pagamento(0, LocalDate.now(), 200.0, MetodoPagamento.CARTAO_DEBITO, consulta.getIdConsulta());
        pagamento.setCodigoTransacao("NSU-TESTE-1234");
        pagamento.setComprovante("Comprovante Teste");
        pagamento = pagamentoRepo.salvar(pagamento);

        assertTrue(pagamento.getIdPagamento() > 0);

        Optional<Pagamento> buscaPag = pagamentoRepo.buscarPorConsultaId(consulta.getIdConsulta());
        assertTrue(buscaPag.isPresent());
        assertEquals("NSU-TESTE-1234", buscaPag.get().getCodigoTransacao());
    }
}
