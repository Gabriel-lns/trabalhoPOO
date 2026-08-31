package com.clinicaveterinaria.repository.sqlite;

import com.clinicaveterinaria.entity.Exame;
import com.clinicaveterinaria.repository.DatabaseManager;
import com.clinicaveterinaria.repository.ExameRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqliteExameRepository implements ExameRepository {

    @Override
    public Exame salvar(Exame exame) {
        String sql = "INSERT INTO exames (tipo, data, resultado, id_consulta_origem, id_prontuario) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, exame.getTipo());
            stmt.setString(2, exame.getData() != null ? exame.getData().toString() : LocalDate.now().toString());
            stmt.setString(3, exame.getResultado());
            stmt.setInt(4, exame.getIdConsultaOrigem());
            stmt.setInt(5, exame.getIdProntuario());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    exame.setIdExame(keys.getInt(1));
                }
            }
            return exame;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar exame: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Exame> listarPorProntuario(int idProntuario) {
        return listarPorQuery("SELECT id_exame, tipo, data, resultado, id_consulta_origem, id_prontuario FROM exames WHERE id_prontuario = ?;", idProntuario);
    }

    @Override
    public List<Exame> listarPorConsulta(int idConsulta) {
        return listarPorQuery("SELECT id_exame, tipo, data, resultado, id_consulta_origem, id_prontuario FROM exames WHERE id_consulta_origem = ?;", idConsulta);
    }

    private List<Exame> listarPorQuery(String sql, int param) {
        List<Exame> lista = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String dtStr = rs.getString("data");
                    LocalDate dt = (dtStr != null && !dtStr.isEmpty()) ? LocalDate.parse(dtStr) : LocalDate.now();
                    lista.add(new Exame(
                            rs.getInt("id_exame"),
                            rs.getString("tipo"),
                            dt,
                            rs.getString("resultado"),
                            rs.getInt("id_consulta_origem"),
                            rs.getInt("id_prontuario")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar exames: " + e.getMessage(), e);
        }
        return lista;
    }
}
