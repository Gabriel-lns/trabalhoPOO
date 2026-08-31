package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class PagamentoRepository {

    public Pagamento salvar(Pagamento pagamento) {
        String sql = "INSERT INTO pagamentos (data_pagamento, valor_pago, metodo_pagamento, id_consulta, codigo_transacao, comprovante) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pagamento.getDataPagamento().toString());
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
            throw new RuntimeException("Erro ao salvar pagamento no banco: " + e.getMessage(), e);
        }
    }

    public Optional<Pagamento> buscarPorConsultaId(int idConsulta) {
        String sql = "SELECT id_pagamento, data_pagamento, valor_pago, metodo_pagamento, id_consulta, codigo_transacao, comprovante FROM pagamentos WHERE id_consulta = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConsulta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pagamento pag = new Pagamento(
                            rs.getInt("id_pagamento"),
                            LocalDate.parse(rs.getString("data_pagamento")),
                            rs.getDouble("valor_pago"),
                            MetodoPagamento.valueOf(rs.getString("metodo_pagamento")),
                            rs.getInt("id_consulta")
                    );
                    pag.setCodigoTransacao(rs.getString("codigo_transacao"));
                    pag.setComprovante(rs.getString("comprovante"));
                    pag.setProcessadoComSucesso(true);
                    return Optional.of(pag);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pagamento da consulta: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
