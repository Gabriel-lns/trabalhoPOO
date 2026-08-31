package com.clinicaveterinaria;

import com.clinicaveterinaria.control.ControladorConsulta;
import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Prontuario;
import com.clinicaveterinaria.entity.Tutor;
import com.clinicaveterinaria.entity.Veterinario;
import com.clinicaveterinaria.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Regras de Negócio e Métodos das Entidades (POO)")
public class RegrasNegocioTest {

    @BeforeAll
    static void initDb() {
        String testDb = "target/test_clinica.db";
        new File("target").mkdirs();
        new File(testDb).delete();
        DatabaseManager.setDatabasePath(testDb);
        DatabaseManager.inicializarBanco();
    }

    @Test
    @DisplayName("Animal.obterIdade() deve calcular a idade correta a partir da data de nascimento")
    void testCalculoIdadeAnimal() {
        LocalDate nasc = LocalDate.now().minusYears(4);
        Animal animal = new Animal(10, "Bidu", "Canina", "Schnauzer", nasc, null);

        assertEquals(4, animal.obterIdade());
    }

    @Test
    @DisplayName("Tutor.obterDadosContato() deve formatar os dados de contato")
    void testDadosContatoTutor() {
        Tutor tutor = new Tutor("123.456.789-00", "Maria Oliveira", "(22) 98888-7777", "Av Central, 50");
        String contato = tutor.obterDadosContato();

        assertTrue(contato.contains("Maria Oliveira"));
        assertTrue(contato.contains("(22) 98888-7777"));
        assertTrue(contato.contains("123.456.789-00"));
    }

    @Test
    @DisplayName("Prontuario.adicionarRegistro() deve criar entradas com carimbo de tempo imutáveis")
    void testProntuarioHistorico() {
        Prontuario p = new Prontuario(1, LocalDate.now(), null);
        p.adicionarRegistro("Animal apresentou tosse seca.");

        assertEquals(1, p.consultarHistorico().size());
        assertTrue(p.consultarHistorico().get(0).contains("Animal apresentou tosse seca."));
    }

    @Test
    @DisplayName("RN09: Conflito de horário do mesmo veterinário")
    void testConflitoHorarioVeterinario() {
        ControladorConsulta ctrl = new ControladorConsulta();
        RepositoryFactory factory = RepositoryFactory.getInstance();
        AnimalRepository animalRepo = factory.getAnimalRepository();
        VeterinarioRepository vetRepo = factory.getVeterinarioRepository();
        TutorRepository tutorRepo = factory.getTutorRepository();

        // Garantir dados base
        Tutor tutor = new Tutor("999.111.222-33", "Teste Tutor", "9999", "Rua X");
        tutorRepo.salvar(tutor);

        Veterinario vet = new Veterinario("CRMV-TESTE-01", "Dr. Teste", "Clínica", "8888");
        vetRepo.salvar(vet);

        Animal a1 = animalRepo.salvar(new Animal(0, "Pet1", "Canina", "SRD", LocalDate.now().minusYears(1), tutor));
        Animal a2 = animalRepo.salvar(new Animal(0, "Pet2", "Felina", "SRD", LocalDate.now().minusYears(2), tutor));

        LocalDateTime agora = LocalDateTime.now().plusDays(5).withHour(10).withMinute(0);

        // Agendar primeira consulta
        ctrl.solicitarAgendamento(vet.getCrmv(), a1.getIdAnimal(), agora, 150.0);

        // Tentar agendar no mesmo horário (diferença de 10 min) para o mesmo veterinário deve disparar exceção RN09
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                ctrl.solicitarAgendamento(vet.getCrmv(), a2.getIdAnimal(), agora.plusMinutes(10), 150.0)
        );

        assertTrue(ex.getMessage().contains("RN09"));
    }
}
