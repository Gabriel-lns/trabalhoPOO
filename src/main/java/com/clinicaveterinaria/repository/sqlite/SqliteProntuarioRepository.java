package com.clinicaveterinaria.repository.sqlite;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Prontuario;
import com.clinicaveterinaria.repository.DatabaseManager;
import com.clinicaveterinaria.repository.ProntuarioRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;

public class SqliteProntuarioRepository implements ProntuarioRepository {

    @Override
    public Prontuario salvar(Prontuario prontuario) {
        String sql = "INSERT OR IGNORE INTO prontuarios (id_animal, data_criacao) VALUES (?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, prontuario.getAnimal() != null ? prontuario.getAnimal().getIdAnimal() : 0);
            stmt.setString(2, prontuario.getDataCriacao() != null ? prontuario.getDataCriacao().toString() : LocalDate.now().toString());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    prontuario.setIdProntuario(keys.getInt(1));
                }
            }
            return prontuario;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar prontuário: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Prontuario> buscarPorAnimalId(int idAnimal, Animal animal) {
        String sql = "SELECT id_prontuario, id_animal, data_criacao FROM prontuarios WHERE id_animal = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAnimal);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idProntuario = rs.getInt("id_prontuario");
                    String dtStr = rs.getString("data_criacao");
                    LocalDate dtCriacao = (dtStr != null && !dtStr.isEmpty()) ? LocalDate.parse(dtStr) : LocalDate.now();

                    Prontuario prontuario = new Prontuario(idProntuario, dtCriacao, animal);
                    carregarHistorico(conn, prontuario);
                    return Optional.of(prontuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar prontuário por animal: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void adicionarRegistroClinico(int idProntuario, String registro) {
        String sql = "INSERT INTO prontuario_historico (id_prontuario, registro) VALUES (?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProntuario);
            stmt.setString(2, registro);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir evolução no prontuário: " + e.getMessage(), e);
        }
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
}
