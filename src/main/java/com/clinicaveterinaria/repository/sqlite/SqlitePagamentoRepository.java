package com.clinicaveterinaria.repository.sqlite;

import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;
import com.clinicaveterinaria.repository.DatabaseManager;
import com.clinicaveterinaria.repository.PagamentoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;

public class SqlitePagamentoRepository implements PagamentoRepository {

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        String sql = "INSERT INTO pagamentos (data_pagamento, valor_pago, metodo_pagamento, id_consulta, codigo_transacao, comprovante) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pagamento.getDataPagamento() != null ? pagamento.getDataPagamento().toString() : LocalDate.now().toString());
            stmt.setDouble(2, pagamento.getValorPago());
            stmt.setString(3, pagamento.getMetodoPagamento().name());
            stmt.setInt(4, pagamento.getIdConsulta());
            stmt.setString(5, pagamento.getCodigoTransacao());
            stmt.setString(6, pagamento.getComprovante());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pagamento.setIdPagamento(generatedKeys.getInt(1));
                }
            }
            return pagamento;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pagamento: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pagamento> buscarPorId(int idPagamento) {
        String sql = "SELECT id_pagamento, data_pagamento, valor_pago, metodo_pagamento, id_consulta, codigo_transacao, comprovante FROM pagamentos WHERE id_pagamento = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPagamento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarPagamento(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pagamento por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Pagamento> buscarPorConsultaId(int idConsulta) {
        String sql = "SELECT id_pagamento, data_pagamento, valor_pago, metodo_pagamento, id_consulta, codigo_transacao, comprovante FROM pagamentos WHERE id_consulta = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConsulta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarPagamento(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pagamento por consulta: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private Pagamento montarPagamento(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_pagamento");
        String dtStr = rs.getString("data_pagamento");
        LocalDate dt = (dtStr != null && !dtStr.isEmpty()) ? LocalDate.parse(dtStr) : LocalDate.now();
        double valor = rs.getDouble("valor_pago");
        MetodoPagamento metodo = MetodoPagamento.fromString(rs.getString("metodo_pagamento"));
        int idConsulta = rs.getInt("id_consulta");
        String cod = rs.getString("codigo_transacao");
        String comp = rs.getString("comprovante");

        return Pagamento.reconstruir(id, dt, valor, metodo, idConsulta, cod, comp);
    }
}
