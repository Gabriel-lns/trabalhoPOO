package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Veterinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VeterinarioRepository {

    public void salvar(Veterinario vet) {
        String sql = "INSERT INTO veterinarios (crmv, nome, especialidade, telefone) VALUES (?, ?, ?, ?)" +
                     " ON CONFLICT(crmv) DO UPDATE SET nome = excluded.nome, especialidade = excluded.especialidade, telefone = excluded.telefone;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vet.getCrmv());
            stmt.setString(2, vet.getNome());
            stmt.setString(3, vet.getEspecialidade());
            stmt.setString(4, vet.getTelefone());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar veterinário no banco: " + e.getMessage(), e);
        }
    }

    public Optional<Veterinario> buscarPorCrmv(String crmv) {
        String sql = "SELECT crmv, nome, especialidade, telefone FROM veterinarios WHERE crmv = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, crmv);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Veterinario(
                            rs.getString("crmv"),
                            rs.getString("nome"),
                            rs.getString("especialidade"),
                            rs.getString("telefone")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar veterinário por CRMV: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Veterinario> listarTodos() {
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT crmv, nome, especialidade, telefone FROM veterinarios ORDER BY nome ASC;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Veterinario(
                        rs.getString("crmv"),
                        rs.getString("nome"),
                        rs.getString("especialidade"),
                        rs.getString("telefone")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar veterinários: " + e.getMessage(), e);
        }
        return lista;
    }
}
