package com.clinicaveterinaria.repository.sqlite;

import com.clinicaveterinaria.entity.Vacina;
import com.clinicaveterinaria.repository.DatabaseManager;
import com.clinicaveterinaria.repository.VacinaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqliteVacinaRepository implements VacinaRepository {

    @Override
    public Vacina salvar(Vacina vacina) {
        String sql = "INSERT INTO vacinas (nome, data_aplicacao, proxima_dose, id_consulta_origem, id_prontuario) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vacina.getNome());
            stmt.setString(2, vacina.getDataAplicacao() != null ? vacina.getDataAplicacao().toString() : LocalDate.now().toString());
            stmt.setString(3, vacina.getProximaDose() != null ? vacina.getProximaDose().toString() : null);
            stmt.setInt(4, vacina.getIdConsultaOrigem());
            stmt.setInt(5, vacina.getIdProntuario());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    vacina.setIdVacina(keys.getInt(1));
                }
            }
            return vacina;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar vacina: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Vacina> listarPorProntuario(int idProntuario) {
        return listarPorQuery("SELECT id_vacina, nome, data_aplicacao, proxima_dose, id_consulta_origem, id_prontuario FROM vacinas WHERE id_prontuario = ?;", idProntuario);
    }

    @Override
    public List<Vacina> listarPorConsulta(int idConsulta) {
        return listarPorQuery("SELECT id_vacina, nome, data_aplicacao, proxima_dose, id_consulta_origem, id_prontuario FROM vacinas WHERE id_consulta_origem = ?;", idConsulta);
    }

    private List<Vacina> listarPorQuery(String sql, int param) {
        List<Vacina> lista = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String dtAppStr = rs.getString("data_aplicacao");
                    LocalDate dtApp = (dtAppStr != null && !dtAppStr.isEmpty()) ? LocalDate.parse(dtAppStr) : LocalDate.now();
                    String dtProxStr = rs.getString("proxima_dose");
                    LocalDate dtProx = (dtProxStr != null && !dtProxStr.isEmpty()) ? LocalDate.parse(dtProxStr) : null;

                    lista.add(new Vacina(
                            rs.getInt("id_vacina"),
                            rs.getString("nome"),
                            dtApp,
                            dtProx,
                            rs.getInt("id_consulta_origem"),
                            rs.getInt("id_prontuario")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vacinas: " + e.getMessage(), e);
        }
        return lista;
    }
}
