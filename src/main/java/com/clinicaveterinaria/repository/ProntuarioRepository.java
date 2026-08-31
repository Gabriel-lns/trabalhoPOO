package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Prontuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class ProntuarioRepository {

    public Optional<Prontuario> buscarPorAnimalId(int idAnimal, Animal animalRef) {
        String sql = "SELECT id_prontuario, data_criacao FROM prontuarios WHERE id_animal = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAnimal);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idProntuario = rs.getInt("id_prontuario");
                    LocalDate dtCriacao = LocalDate.parse(rs.getString("data_criacao"));
                    Prontuario prontuario = new Prontuario(idProntuario, dtCriacao, animalRef);

                    // Carregar histórico
                    carregarHistorico(conn, prontuario);

                    return Optional.of(prontuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar prontuário do animal: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private void carregarHistorico(Connection conn, Prontuario prontuario) throws SQLException {
        String sql = "SELECT registro FROM prontuario_historico WHERE id_prontuario = ? ORDER BY id_historico ASC;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, prontuario.getIdProntuario());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prontuario.carregarRegistroBruto(rs.getString("registro"));
                }
            }
        }
    }

    public void adicionarRegistroClinico(int idProntuario, String registro) {
        String sql = "INSERT INTO prontuario_historico (id_prontuario, registro) VALUES (?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProntuario);
            stmt.setString(2, registro);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar evolução no prontuário: " + e.getMessage(), e);
        }
    }
}
