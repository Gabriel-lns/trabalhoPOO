package com.clinicaveterinaria.repository.sqlite;

import com.clinicaveterinaria.entity.Tutor;
import com.clinicaveterinaria.repository.DatabaseManager;
import com.clinicaveterinaria.repository.TutorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteTutorRepository implements TutorRepository {

    @Override
    public Tutor salvar(Tutor tutor) {
        String sql = "INSERT OR REPLACE INTO tutores (cpf, nome, telefone, endereco) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tutor.getCpf());
            stmt.setString(2, tutor.getNome());
            stmt.setString(3, tutor.getTelefone());
            stmt.setString(4, tutor.getEndereco());
            stmt.executeUpdate();
            return tutor;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar tutor no SQLite: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Tutor> buscarPorCpf(String cpf) {
        String sql = "SELECT cpf, nome, telefone, endereco FROM tutores WHERE cpf = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Tutor(
                            rs.getString("cpf"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("endereco")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tutor por CPF: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Tutor> listarTodos() {
        List<Tutor> lista = new ArrayList<>();
        String sql = "SELECT cpf, nome, telefone, endereco FROM tutores ORDER BY nome ASC;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Tutor(
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("endereco")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tutores: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public boolean deletar(String cpf) {
        String sql = "DELETE FROM tutores WHERE cpf = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar tutor: " + e.getMessage(), e);
        }
    }
}
