package com.clinicaveterinaria.repository.sqlite;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Veterinario;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.repository.AnimalRepository;
import com.clinicaveterinaria.repository.ConsultaRepository;
import com.clinicaveterinaria.repository.DatabaseManager;
import com.clinicaveterinaria.repository.VeterinarioRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteConsultaRepository implements ConsultaRepository {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final AnimalRepository animalRepository;
    private final VeterinarioRepository veterinarioRepository;

    public SqliteConsultaRepository() {
        this.animalRepository = new SqliteAnimalRepository();
        this.veterinarioRepository = new SqliteVeterinarioRepository();
    }

    public SqliteConsultaRepository(AnimalRepository animalRepository, VeterinarioRepository veterinarioRepository) {
        this.animalRepository = animalRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @Override
    public Consulta salvar(Consulta consulta) {
        String sql = "INSERT INTO consultas (data_hora, status, valor, observacoes, id_animal, crmv_veterinario) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, consulta.getDataHora().format(FORMATTER));
            stmt.setString(2, consulta.getStatus().getDescricao());
            stmt.setDouble(3, consulta.getValor());
            stmt.setString(4, consulta.getObservacoes());
            stmt.setInt(5, consulta.getAnimal() != null ? consulta.getAnimal().getIdAnimal() : 0);
            stmt.setString(6, consulta.getVeterinario() != null ? consulta.getVeterinario().getCrmv() : null);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    consulta.setIdConsulta(generatedKeys.getInt(1));
                }
            }
            return consulta;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar consulta: " + e.getMessage(), e);
        }
    }

    @Override
    public Consulta atualizar(Consulta consulta) {
        String sql = "UPDATE consultas SET data_hora = ?, status = ?, valor = ?, observacoes = ?, id_animal = ?, crmv_veterinario = ? WHERE id_consulta = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, consulta.getDataHora().format(FORMATTER));
            stmt.setString(2, consulta.getStatus().getDescricao());
            stmt.setDouble(3, consulta.getValor());
            stmt.setString(4, consulta.getObservacoes());
            stmt.setInt(5, consulta.getAnimal() != null ? consulta.getAnimal().getIdAnimal() : 0);
            stmt.setString(6, consulta.getVeterinario() != null ? consulta.getVeterinario().getCrmv() : null);
            stmt.setInt(7, consulta.getIdConsulta());

            stmt.executeUpdate();
            return consulta;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar consulta: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Consulta> buscarPorId(int idConsulta) {
        String sql = "SELECT id_consulta, data_hora, status, valor, observacoes, id_animal, crmv_veterinario FROM consultas WHERE id_consulta = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConsulta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarConsulta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar consulta por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Consulta> listarTodas() {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT id_consulta, data_hora, status, valor, observacoes, id_animal, crmv_veterinario FROM consultas ORDER BY data_hora DESC;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montarConsulta(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar consultas: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Consulta> listarPorStatus(StatusConsulta status) {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT id_consulta, data_hora, status, valor, observacoes, id_animal, crmv_veterinario FROM consultas WHERE status = ? ORDER BY data_hora ASC;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.getDescricao());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarConsulta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar consultas por status: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Consulta> buscarPorVeterinarioEPeriodo(String crmv, LocalDateTime inicio, LocalDateTime fim) {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT id_consulta, data_hora, status, valor, observacoes, id_animal, crmv_veterinario FROM consultas WHERE crmv_veterinario = ? AND status != 'Cancelada' AND data_hora BETWEEN ? AND ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, crmv);
            stmt.setString(2, inicio.format(FORMATTER));
            stmt.setString(3, fim.format(FORMATTER));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarConsulta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conflito de consultas: " + e.getMessage(), e);
        }
        return lista;
    }

    private Consulta montarConsulta(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_consulta");
        LocalDateTime dt = LocalDateTime.parse(rs.getString("data_hora"), FORMATTER);
        StatusConsulta status = StatusConsulta.fromString(rs.getString("status"));
        double valor = rs.getDouble("valor");
        String obs = rs.getString("observacoes");
        int idAnimal = rs.getInt("id_animal");
        String crmv = rs.getString("crmv_veterinario");

        Animal animal = animalRepository.buscarPorId(idAnimal).orElse(null);
        Veterinario vet = veterinarioRepository.buscarPorCrmv(crmv).orElse(null);

        return Consulta.reconstruir(id, dt, valor, status, obs, animal, vet);
    }
}
