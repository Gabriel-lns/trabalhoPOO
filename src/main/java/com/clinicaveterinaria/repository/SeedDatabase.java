package com.clinicaveterinaria.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Popula o banco de dados com dados iniciais fictícios para teste e apresentação imediata.
 */
public class SeedDatabase {

    public static void executar(Connection conn) {
        try {
            // Verificar se já existem tutores
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM tutores;");
                 ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return; // Já populado
                }
            }

            System.out.println("Populando banco de dados inicial da Clínica Veterinária...");

            // 1. Inserir Tutores
            String sqlTutor = "INSERT OR IGNORE INTO tutores (cpf, nome, telefone, endereco) VALUES (?, ?, ?, ?);";
            try (PreparedStatement stmt = conn.prepareStatement(sqlTutor)) {
                stmt.setString(1, "111.222.333-44");
                stmt.setString(2, "Gabriel Nunes");
                stmt.setString(3, "(22) 99888-1122");
                stmt.setString(4, "Rua das Palmeiras, 120, Centro");
                stmt.executeUpdate();

                stmt.setString(1, "555.666.777-88");
                stmt.setString(2, "Emily Silva");
                stmt.setString(3, "(22) 99777-3344");
                stmt.setString(4, "Av. Atlântica, 450, Praia Campista");
                stmt.executeUpdate();

                stmt.setString(1, "999.888.777-66");
                stmt.setString(2, "Carlos Eduardo Souza");
                stmt.setString(3, "(22) 99555-8899");
                stmt.setString(4, "Rua Silva Jardim, 88, Bairro Glória");
                stmt.executeUpdate();
            }

            // 2. Inserir Veterinários
            String sqlVet = "INSERT OR IGNORE INTO veterinarios (crmv, nome, especialidade, telefone) VALUES (?, ?, ?, ?);";
            try (PreparedStatement stmt = conn.prepareStatement(sqlVet)) {
                stmt.setString(1, "CRMV-RJ 12345");
                stmt.setString(2, "Dra. Juliana Mendes");
                stmt.setString(3, "Clínica Geral & Cirurgia");
                stmt.setString(4, "(22) 98111-2233");
                stmt.executeUpdate();

                stmt.setString(1, "CRMV-RJ 67890");
                stmt.setString(2, "Dr. Roberto Costa");
                stmt.setString(3, "Dermatologia & Felinos");
                stmt.setString(4, "(22) 98222-4455");
                stmt.executeUpdate();
            }

            // 3. Inserir Animais
            String sqlAnimal = "INSERT OR IGNORE INTO animais (nome, especie, raca, data_nascimento, tutor_cpf) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement stmt = conn.prepareStatement(sqlAnimal)) {
                stmt.setString(1, "Thor");
                stmt.setString(2, "Canina");
                stmt.setString(3, "Golden Retriever");
                stmt.setString(4, LocalDate.now().minusYears(3).toString());
                stmt.setString(5, "111.222.333-44");
                stmt.executeUpdate();

                stmt.setString(1, "Luna");
                stmt.setString(2, "Felina");
                stmt.setString(3, "Siamês");
                stmt.setString(4, LocalDate.now().minusYears(2).toString());
                stmt.setString(5, "555.666.777-88");
                stmt.executeUpdate();

                stmt.setString(1, "Mel");
                stmt.setString(2, "Canina");
                stmt.setString(3, "Poodle Toy");
                stmt.setString(4, LocalDate.now().minusYears(5).toString());
                stmt.setString(5, "999.888.777-66");
                stmt.executeUpdate();
            }

            // 4. Inserir Prontuários
            String sqlProntuario = "INSERT OR IGNORE INTO prontuarios (id_animal, data_criacao) VALUES (?, ?);";
            try (PreparedStatement stmt = conn.prepareStatement(sqlProntuario)) {
                stmt.setInt(1, 1);
                stmt.setString(2, LocalDate.now().minusYears(1).toString());
                stmt.executeUpdate();

                stmt.setInt(1, 2);
                stmt.setString(2, LocalDate.now().minusYears(1).toString());
                stmt.executeUpdate();

                stmt.setInt(1, 3);
                stmt.setString(2, LocalDate.now().minusYears(2).toString());
                stmt.executeUpdate();
            }

            // 5. Inserir Histórico nos Prontuários
            String sqlHist = "INSERT INTO prontuario_historico (id_prontuario, registro) VALUES (?, ?);";
            try (PreparedStatement stmt = conn.prepareStatement(sqlHist)) {
                stmt.setInt(1, 1);
                stmt.setString(2, "[Cadastro Inicial] Abertura de prontuário e vacinação antirrábica inicial.");
                stmt.executeUpdate();

                stmt.setInt(1, 2);
                stmt.setString(2, "[Cadastro Inicial] Paciente saudável, peso 4.2kg, sem queixas clínicas.");
                stmt.executeUpdate();
            }

            // 6. Inserir Consultas de Demonstração (em diferentes estados)
            String sqlConsulta = "INSERT INTO consultas (data_hora, status, valor, observacoes, id_animal, crmv_veterinario) VALUES (?, ?, ?, ?, ?, ?);";
            try (PreparedStatement stmt = conn.prepareStatement(sqlConsulta)) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                // Consulta 1: Agendada para hoje
                stmt.setString(1, LocalDateTime.now().plusHours(1).format(fmt));
                stmt.setString(2, "Agendada");
                stmt.setDouble(3, 180.0);
                stmt.setString(4, "Consulta de rotina anual e checagem de peso.");
                stmt.setInt(5, 1); // Thor
                stmt.setString(6, "CRMV-RJ 12345");
                stmt.executeUpdate();

                // Consulta 2: Realizada aguardando pagamento no caixa
                stmt.setString(1, LocalDateTime.now().minusHours(2).format(fmt));
                stmt.setString(2, "Realizada");
                stmt.setDouble(3, 250.0);
                stmt.setString(4, "Avaliação dermatológica concluída. Prescrito shampoo hipoalergênico.");
                stmt.setInt(5, 2); // Luna
                stmt.setString(6, "CRMV-RJ 67890");
                stmt.executeUpdate();

                // Consulta 3: Concluída e Paga anteriormente
                stmt.setString(1, LocalDateTime.now().minusDays(10).format(fmt));
                stmt.setString(2, "Paga");
                stmt.setDouble(3, 150.0);
                stmt.setString(4, "Vacinação V10 aplicada com sucesso.");
                stmt.setInt(5, 3); // Mel
                stmt.setString(6, "CRMV-RJ 12345");
                stmt.executeUpdate();
            }

            // 7. Pagamento da Consulta 3
            String sqlPagamento = "INSERT OR IGNORE INTO pagamentos (data_pagamento, valor_pago, metodo_pagamento, id_consulta, codigo_transacao, comprovante) VALUES (?, ?, ?, ?, ?, ?);";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPagamento)) {
                stmt.setString(1, LocalDate.now().minusDays(10).toString());
                stmt.setDouble(2, 150.0);
                stmt.setString(3, "PIX");
                stmt.setInt(4, 3);
                stmt.setString(5, "PIX-DEMO-9988");
                stmt.setString(6, "Comprovante PIX Autenticado - R$ 150.00");
                stmt.executeUpdate();
            }

            System.out.println("Banco de dados populado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Aviso ao popular dados iniciais: " + e.getMessage());
        }
    }
}
