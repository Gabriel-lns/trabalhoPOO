package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Tutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TutorRepository {

    public void salvar(Tutor tutor) {
        String sql = "INSERT INTO tutores (cpf, nome, telefone, endereco) VALUES (?, ?, ?, ?)" +
                     " ON CONFLICT(cpf) DO UPDATE SET nome = excluded.nome, telefone = excluded.telefone, endereco = excluded.endereco;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tutor.getCpf());
            stmt.setString(2, tutor.getNome());
            stmt.setString(3, tutor.getTelefone());
            stmt.setString(4, tutor.getEndereco());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar tutor no banco: " + e.getMessage(), e);
        }
    }

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
}
