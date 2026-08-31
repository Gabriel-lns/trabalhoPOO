package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Exame;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExameRepository {

    public Exame salvar(Exame exame) {
        String sql = "INSERT INTO exames (tipo, data, resultado, id_consulta_origem, id_prontuario) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, exame.getTipo());
            stmt.setString(2, exame.getData().toString());
            stmt.setString(3, exame.getResultado());
            stmt.setInt(4, exame.getIdConsultaOrigem());
            stmt.setInt(5, exame.getIdProntuario());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exame.setIdExame(generatedKeys.getInt(1));
                }
            }
            return exame;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar exame: " + e.getMessage(), e);
        }
    }

    public List<Exame> listarPorProntuario(int idProntuario) {
        List<Exame> lista = new ArrayList<>();
        String sql = "SELECT id_exame, tipo, data, resultado, id_consulta_origem, id_prontuario FROM exames WHERE id_prontuario = ? ORDER BY data DESC;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProntuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Exame(
                            rs.getInt("id_exame"),
                            rs.getString("tipo"),
                            LocalDate.parse(rs.getString("data")),
                            rs.getString("resultado"),
                            rs.getInt("id_consulta_origem"),
                            rs.getInt("id_prontuario")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar exames do prontuário: " + e.getMessage(), e);
        }
        return lista;
    }
}
