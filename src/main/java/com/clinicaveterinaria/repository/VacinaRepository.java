package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Vacina;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VacinaRepository {

    public Vacina salvar(Vacina vacina) {
        String sql = "INSERT INTO vacinas (nome, data_aplicacao, proxima_dose, id_consulta_origem, id_prontuario) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vacina.getNome());
            stmt.setString(2, vacina.getDataAplicacao().toString());
            stmt.setString(3, vacina.getProximaDose() != null ? vacina.getProximaDose().toString() : null);
            stmt.setInt(4, vacina.getIdConsultaOrigem());
            stmt.setInt(5, vacina.getIdProntuario());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vacina.setIdVacina(generatedKeys.getInt(1));
                }
            }
            return vacina;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar vacina: " + e.getMessage(), e);
        }
    }

    public List<Vacina> listarPorProntuario(int idProntuario) {
        List<Vacina> lista = new ArrayList<>();
        String sql = "SELECT id_vacina, nome, data_aplicacao, proxima_dose, id_consulta_origem, id_prontuario FROM vacinas WHERE id_prontuario = ? ORDER BY data_aplicacao DESC;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProntuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate dtProx = rs.getString("proxima_dose") != null ? LocalDate.parse(rs.getString("proxima_dose")) : null;
                    lista.add(new Vacina(
                            rs.getInt("id_vacina"),
                            rs.getString("nome"),
                            LocalDate.parse(rs.getString("data_aplicacao")),
                            dtProx,
                            rs.getInt("id_consulta_origem"),
                            rs.getInt("id_prontuario")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vacinas do prontuário: " + e.getMessage(), e);
        }
        return lista;
    }
}
