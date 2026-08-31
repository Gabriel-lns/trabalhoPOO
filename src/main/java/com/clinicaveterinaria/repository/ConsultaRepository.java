package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.Veterinario;
import com.clinicaveterinaria.entity.enums.StatusConsulta;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultaRepository {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final AnimalRepository animalRepository = new AnimalRepository();
    private final VeterinarioRepository veterinarioRepository = new VeterinarioRepository();
    private final PagamentoRepository pagamentoRepository = new PagamentoRepository();

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

    public void atualizar(Consulta consulta) {
        String sql = "UPDATE consultas SET status = ?, valor = ?, observacoes = ? WHERE id_consulta = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, consulta.getStatus().getDescricao());
            stmt.setDouble(2, consulta.getValor());
            stmt.setString(3, consulta.getObservacoes());
            stmt.setInt(4, consulta.getIdConsulta());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar consulta: " + e.getMessage(), e);
        }
    }

    public Optional<Consulta> buscarPorId(int id) {
        String sql = "SELECT id_consulta, data_hora, status, valor, observacoes, id_animal, crmv_veterinario FROM consultas WHERE id_consulta = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
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

    public List<Consulta> listarTodos() {
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

    /**
     * Busca consultas de um veterinário específico em uma data/hora para validar conflito (RN09 / SD01).
     */
    public List<Consulta> buscarConsultasPorVeterinarioEData(String crmv, LocalDateTime dataHora) {
        List<Consulta> lista = new ArrayList<>();
        // Considera conflito em um intervalo de +/- 30 minutos
        String sql = "SELECT id_consulta, data_hora, status, valor, observacoes, id_animal, crmv_veterinario " +
                     "FROM consultas WHERE crmv_veterinario = ? AND status != 'Cancelada';";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, crmv);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Consulta c = montarConsulta(rs);
                    // Checagem de proximidade temporal (30 min)
                    long diffMinutos = Math.abs(java.time.Duration.between(c.getDataHora(), dataHora).toMinutes());
                    if (diffMinutos < 30) {
                        lista.add(c);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao checar conflitos de agenda: " + e.getMessage(), e);
        }
        return lista;
    }

    private Consulta montarConsulta(ResultSet rs) throws SQLException {
        int idConsulta = rs.getInt("id_consulta");
        LocalDateTime dataHora = LocalDateTime.parse(rs.getString("data_hora"), FORMATTER);
        String statusStr = rs.getString("status");
        double valor = rs.getDouble("valor");
        String obs = rs.getString("observacoes");
        int idAnimal = rs.getInt("id_animal");
        String crmvVet = rs.getString("crmv_veterinario");

        Animal animal = animalRepository.buscarPorId(idAnimal).orElse(null);
        Veterinario vet = crmvVet != null ? veterinarioRepository.buscarPorCrmv(crmvVet).orElse(null) : null;

        Consulta c = new Consulta(idConsulta, dataHora, valor, animal, vet);
        c.setStatus(statusStr);
        c.setObservacoes(obs != null ? obs : "");

        // Carregar pagamento se houver
        pagamentoRepository.buscarPorConsultaId(idConsulta).ifPresent(c::setPagamento);

        return c;
    }
}
