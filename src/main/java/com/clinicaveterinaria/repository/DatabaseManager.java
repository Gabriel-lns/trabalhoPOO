package com.clinicaveterinaria.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerenciador central de conexões e schema do banco de dados SQLite.
 */
public class DatabaseManager {
    private static final String DEFAULT_DB_PATH = "clinica_veterinaria.db";
    private static String dbUrl = "jdbc:sqlite:" + DEFAULT_DB_PATH;

    public static void setDatabasePath(String path) {
        dbUrl = "jdbc:sqlite:" + path;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Inicializa todas as tabelas relacionais do sistema.
     */
    public static void inicializarBanco() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Habilitar chaves estrangeiras no SQLite
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Tabela Tutor
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tutores (
                    cpf TEXT PRIMARY KEY,
                    nome TEXT NOT NULL,
                    telefone TEXT,
                    endereco TEXT
                );
            """);

            // Tabela Veterinario
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS veterinarios (
                    crmv TEXT PRIMARY KEY,
                    nome TEXT NOT NULL,
                    especialidade TEXT,
                    telefone TEXT
                );
            """);

            // Tabela Animal
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS animais (
                    id_animal INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    especie TEXT,
                    raca TEXT,
                    data_nascimento TEXT,
                    tutor_cpf TEXT NOT NULL,
                    FOREIGN KEY (tutor_cpf) REFERENCES tutores(cpf) ON DELETE RESTRICT
                );
            """);

            // Tabela Prontuario
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS prontuarios (
                    id_prontuario INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_animal INTEGER UNIQUE NOT NULL,
                    data_criacao TEXT NOT NULL,
                    FOREIGN KEY (id_animal) REFERENCES animais(id_animal) ON DELETE RESTRICT
                );
            """);

            // Tabela de Itens de Historico do Prontuario
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS prontuario_historico (
                    id_historico INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_prontuario INTEGER NOT NULL,
                    registro TEXT NOT NULL,
                    FOREIGN KEY (id_prontuario) REFERENCES prontuarios(id_prontuario) ON DELETE RESTRICT
                );
            """);

            // Tabela Consulta
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS consultas (
                    id_consulta INTEGER PRIMARY KEY AUTOINCREMENT,
                    data_hora TEXT NOT NULL,
                    status TEXT NOT NULL,
                    valor REAL NOT NULL,
                    observacoes TEXT,
                    id_animal INTEGER NOT NULL,
                    crmv_veterinario TEXT NOT NULL,
                    FOREIGN KEY (id_animal) REFERENCES animais(id_animal),
                    FOREIGN KEY (crmv_veterinario) REFERENCES veterinarios(crmv)
                );
            """);

            // Tabela Exame
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS exames (
                    id_exame INTEGER PRIMARY KEY AUTOINCREMENT,
                    tipo TEXT NOT NULL,
                    data TEXT NOT NULL,
                    resultado TEXT,
                    id_consulta_origem INTEGER,
                    id_prontuario INTEGER,
                    FOREIGN KEY (id_consulta_origem) REFERENCES consultas(id_consulta),
                    FOREIGN KEY (id_prontuario) REFERENCES prontuarios(id_prontuario)
                );
            """);

            // Tabela Vacina
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS vacinas (
                    id_vacina INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    data_aplicacao TEXT NOT NULL,
                    proxima_dose TEXT,
                    id_consulta_origem INTEGER,
                    id_prontuario INTEGER,
                    FOREIGN KEY (id_consulta_origem) REFERENCES consultas(id_consulta),
                    FOREIGN KEY (id_prontuario) REFERENCES prontuarios(id_prontuario)
                );
            """);

            // Tabela Pagamento
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pagamentos (
                    id_pagamento INTEGER PRIMARY KEY AUTOINCREMENT,
                    data_pagamento TEXT NOT NULL,
                    valor_pago REAL NOT NULL,
                    metodo_pagamento TEXT NOT NULL,
                    id_consulta INTEGER UNIQUE NOT NULL,
                    codigo_transacao TEXT,
                    comprovante TEXT,
                    FOREIGN KEY (id_consulta) REFERENCES consultas(id_consulta)
                );
            """);

            // Popular dados iniciais se vazio
            SeedDatabase.executar(conn);

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados SQLite: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
