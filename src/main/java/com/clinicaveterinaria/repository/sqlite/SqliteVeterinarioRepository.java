package com.clinicaveterinaria.repository.sqlite;

import com.clinicaveterinaria.entity.Veterinario;
import com.clinicaveterinaria.repository.DatabaseManager;
import com.clinicaveterinaria.repository.VeterinarioRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteVeterinarioRepository implements VeterinarioRepository {

    @Override
    public Veterinario salvar(Veterinario veterinario) {
        String sql = "INSERT OR REPLACE INTO veterinarios (crmv, nome, especialidade, telefone) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, veterinario.getCrmv());
            stmt.setString(2, veterinario.getNome());
            stmt.setString(3, veterinario.getEspecialidade());
            stmt.setString(4, veterinario.getTelefone());
            stmt.executeUpdate();
            return veterinario;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar veterinário: " + e.getMessage(), e);
        }
    }

    @Override
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

    @Override
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
