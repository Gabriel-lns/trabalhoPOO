package com.clinicaveterinaria;

import com.clinicaveterinaria.entity.*;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários Exaustivos das Entidades de Domínio")
public class EntidadesUnitTest {

    @Test
    @DisplayName("Tutor: construtor, validação, métodos de contato e lista de animais")
    void testTutorCompleto() {
        Tutor tutor = new Tutor("123.456.789-01", "Ana Paula", "(21) 98765-4321", "Rua das Flores, 100");

        assertEquals("123.456.789-01", tutor.getCpf());
        assertEquals("Ana Paula", tutor.getNome());
        assertEquals("(21) 98765-4321", tutor.getTelefone());
        assertEquals("Rua das Flores, 100", tutor.getEndereco());

        String contato = tutor.obterDadosContato();
        assertTrue(contato.contains("Ana Paula"));
        assertTrue(contato.contains("(21) 98765-4321"));
        assertTrue(contato.contains("Rua das Flores, 100"));
        assertTrue(contato.contains("123.456.789-01"));

        // Adicionar e remover animal
        Animal animal = new Animal(1, "Rex", "Canina", "Pastor Alemão", LocalDate.now().minusYears(3), tutor);
        tutor.adicionarAnimal(animal);
        assertEquals(1, tutor.getAnimais().size());

        tutor.removerAnimal(animal);
        assertEquals(0, tutor.getAnimais().size());

        // Equals, hashCode e toString
        Tutor tutor2 = new Tutor("123.456.789-01", "Outro Nome", "", "");
        assertEquals(tutor, tutor2);
        assertEquals(tutor.hashCode(), tutor2.hashCode());
        assertTrue(tutor.toString().contains("Ana Paula"));
    }

    @Test
    @DisplayName("Animal: cálculo de idade, getters/setters e vinculação")
    void testAnimalCompleto() {
        Tutor tutor = new Tutor("111.111.111-11", "Lucas", "1111", "Rua 1");
        LocalDate dtNasc = LocalDate.now().minusYears(5).minusMonths(2);
        Animal animal = new Animal(10, "Boby", "Canina", "Beagle", dtNasc, tutor);

        assertEquals(10, animal.getIdAnimal());
        assertEquals("Boby", animal.getNome());
        assertEquals("Canina", animal.getEspecie());
        assertEquals("Beagle", animal.getRaca());
        assertEquals(5, animal.obterIdade());
        assertEquals(tutor, animal.getTutor());

        // Testar alteração de atributos
        animal.setNome("Boby Silva");
        animal.setEspecie("Felina");
        animal.setRaca("Persa");
        assertEquals("Boby Silva", animal.getNome());
        assertEquals("Felina", animal.getEspecie());
        assertEquals("Persa", animal.getRaca());

        // Equals e toString
        Animal animal2 = new Animal(10, "Outro", "Outra", "Outra", null, null);
        assertEquals(animal, animal2);
        assertEquals(animal.hashCode(), animal2.hashCode());
        assertTrue(animal.toString().contains("Boby Silva"));
    }

    @Test
    @DisplayName("Veterinario: crmv, crvm alias e dados")
    void testVeterinarioCompleto() {
        Veterinario vet = new Veterinario("CRMV-SP 54321", "Dr. Fernando", "Ortopedia", "11999998888");

        assertEquals("CRMV-SP 54321", vet.getCrmv());
        assertEquals("CRMV-SP 54321", vet.getCrvm()); // Alias compatível com Astah
        assertEquals("Dr. Fernando", vet.getNome());
        assertEquals("Ortopedia", vet.getEspecialidade());
        assertEquals("11999998888", vet.getTelefone());

        vet.setCrvm("CRMV-SP 99999");
        assertEquals("CRMV-SP 99999", vet.getCrmv());

        Veterinario vet2 = new Veterinario("CRMV-SP 99999", "Qualquer", "Geral", "");
        assertEquals(vet, vet2);
        assertEquals(vet.hashCode(), vet2.hashCode());
        assertTrue(vet.toString().contains("Dr(a). Dr. Fernando"));
    }

    @Test
    @DisplayName("Prontuario: evolução imutável, exames e vacinas vinculadas")
    void testProntuarioCompleto() {
        Prontuario prontuario = new Prontuario(1, LocalDate.now(), null);
        assertEquals(1, prontuario.getIdProntuario());
        assertNotNull(prontuario.getDataCriacao());

        // Adicionar registros
        prontuario.adicionarRegistro("Primeira consulta realizada com sucesso.");
        prontuario.adicionarRegistro("Retorno em 15 dias para reavaliação.");

        assertEquals(2, prontuario.consultarHistorico().size());
        assertTrue(prontuario.consultarHistorico().get(0).contains("Primeira consulta realizada"));

        // Adicionar exame
        Exame exame = new Exame(100, "Hemograma", LocalDate.now(), "Plaquetas normais", 1, 1);
        prontuario.adicionarExame(exame);
        assertEquals(1, prontuario.getExames().size());

        // Adicionar vacina
        Vacina vacina = new Vacina(200, "Raiva", LocalDate.now(), LocalDate.now().plusYears(1), 1, 1);
        prontuario.adicionarVacina(vacina);
        assertEquals(1, prontuario.getVacinas().size());
    }

    @Test
    @DisplayName("Exame: anexação de resultado e propriedades")
    void testExameCompleto() {
        Exame exame = new Exame(1, "Raio-X Tórax", LocalDate.now(), "Aguardando laudo", 10, 20);

        assertEquals(1, exame.getIdExame());
        assertEquals("Raio-X Tórax", exame.getTipo());
        assertEquals("Aguardando laudo", exame.getResultado());
        assertEquals(10, exame.getIdConsultaOrigem());
        assertEquals(20, exame.getIdProntuario());

        exame.anexarResultado("Sem alterações pulmonares visíveis.");
        assertEquals("Sem alterações pulmonares visíveis.", exame.getResultado());

        Exame exame2 = new Exame(1, "Outro", null, "", 0, 0);
        assertEquals(exame, exame2);
        assertEquals(exame.hashCode(), exame2.hashCode());
        assertTrue(exame.toString().contains("Raio-X Tórax"));
    }

    @Test
    @DisplayName("Vacina: propriedades e datas de aplicação")
    void testVacinaCompleto() {
        LocalDate hoje = LocalDate.now();
        LocalDate reforco = hoje.plusYears(1);
        Vacina vacina = new Vacina(1, "V8 Canina", hoje, reforco, 5, 10);

        assertEquals(1, vacina.getIdVacina());
        assertEquals("V8 Canina", vacina.getNome());
        assertEquals(hoje, vacina.getDataAplicacao());
        assertEquals(reforco, vacina.getProximaDose());
        assertEquals(5, vacina.getIdConsultaOrigem());
        assertEquals(10, vacina.getIdProntuario());

        Vacina vacina2 = new Vacina(1, "Outra", null, null, 0, 0);
        assertEquals(vacina, vacina2);
        assertEquals(vacina.hashCode(), vacina2.hashCode());
        assertTrue(vacina.toString().contains("V8 Canina"));
    }

    @Test
    @DisplayName("Pagamento: processamento via Strategy e propriedades")
    void testPagamentoCompleto() {
        Pagamento pag = new Pagamento(1, LocalDate.now(), 250.0, MetodoPagamento.CARTAO_CREDITO, 3);
        Consulta c = new Consulta(3, LocalDateTime.now(), 250.0, null, null);

        boolean sucesso = pag.processarPagamento(c);
        assertTrue(sucesso);
        assertTrue(pag.isProcessadoComSucesso());
        assertNotNull(pag.getCodigoTransacao());
        assertNotNull(pag.getComprovante());

        // Processar sobrecarga sem argumentos
        Pagamento pag2 = new Pagamento(2, LocalDate.now(), 100.0, MetodoPagamento.PIX, 4);
        assertTrue(pag2.processarPagamento());

        assertEquals(1, pag.getIdPagamento());
        assertEquals(250.0, pag.getValorPago());
        assertEquals(MetodoPagamento.CARTAO_CREDITO, pag.getMetodoPagamento());
        assertEquals(3, pag.getIdConsulta());
        assertTrue(pag.toString().contains("250,00") || pag.toString().contains("250.00"));
    }

    @Test
    @DisplayName("Enums: StatusConsulta e MetodoPagamento helpers")
    void testEnumsHelpers() {
        assertEquals(StatusConsulta.AGENDADA, StatusConsulta.fromString("Agendada"));
        assertEquals(StatusConsulta.EM_ANDAMENTO, StatusConsulta.fromString("EM_ANDAMENTO"));
        assertEquals(StatusConsulta.REALIZADA, StatusConsulta.fromString("Realizada"));
        assertEquals(StatusConsulta.PAGA, StatusConsulta.fromString("Paga"));
        assertEquals(StatusConsulta.CANCELADA, StatusConsulta.fromString("Cancelada"));
        assertEquals(StatusConsulta.AGENDADA, StatusConsulta.fromString("invalido"));

        assertEquals(MetodoPagamento.PIX, MetodoPagamento.fromString("PIX"));
        assertEquals(MetodoPagamento.CARTAO_CREDITO, MetodoPagamento.fromString("Cartão de Crédito"));
        assertEquals(MetodoPagamento.CARTAO_DEBITO, MetodoPagamento.fromString("Cartão de Débito"));
        assertEquals(MetodoPagamento.DINHEIRO, MetodoPagamento.fromString("Dinheiro em Espécie"));
        assertEquals(MetodoPagamento.PIX, MetodoPagamento.fromString("invalido"));
    }
}
