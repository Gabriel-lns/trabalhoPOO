package com.clinicaveterinaria.boundary;

import com.clinicaveterinaria.control.ControladorCadastros;
import com.clinicaveterinaria.control.ControladorConsulta;
import com.clinicaveterinaria.control.ControladorFinanceiro;
import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.enums.StatusConsulta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaDashboardPanel extends JPanel {
    private final ControladorConsulta controladorConsulta;
    private final ControladorFinanceiro controladorFinanceiro;
    private final ControladorCadastros controladorCadastros;

    private JLabel lblTotalConsultas;
    private JLabel lblConsultasPagas;
    private JLabel lblFaturamentoTotal;
    private JLabel lblTotalAnimais;

    private JTable tabelaRecentes;
    private DefaultTableModel tableModel;

    public TelaDashboardPanel(ControladorConsulta controladorConsulta, ControladorFinanceiro controladorFinanceiro, ControladorCadastros controladorCadastros) {
        this.controladorConsulta = controladorConsulta;
        this.controladorFinanceiro = controladorFinanceiro;
        this.controladorCadastros = controladorCadastros;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        recarregar();
    }

    private void initComponents() {
        // Painel Superior de Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 15));

        lblTotalConsultas = new JLabel("0", SwingConstants.CENTER);
        lblConsultasPagas = new JLabel("0", SwingConstants.CENTER);
        lblFaturamentoTotal = new JLabel("R$ 0,00", SwingConstants.CENTER);
        lblTotalAnimais = new JLabel("0", SwingConstants.CENTER);

        cardsPanel.add(criarCard("📋 Total de Consultas", lblTotalConsultas, new Color(52, 152, 219)));
        cardsPanel.add(criarCard("✅ Consultas Pagas", lblConsultasPagas, new Color(46, 204, 113)));
        cardsPanel.add(criarCard("💰 Faturamento Total", lblFaturamentoTotal, new Color(241, 196, 15)));
        cardsPanel.add(criarCard("🐾 Animais Cadastrados", lblTotalAnimais, new Color(155, 89, 182)));

        add(cardsPanel, BorderLayout.NORTH);

        // Painel Central: Tabela de Consultas Recentes
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        JLabel lblTabela = new JLabel("Visão Geral das Consultas no Sistema");
        lblTabela.setFont(new Font("SansSerif", Font.BOLD, 14));
        centerPanel.add(lblTabela, BorderLayout.NORTH);

        String[] colunas = {"ID", "Data / Hora", "Paciente (Animal)", "Espécie/Raça", "Tutor", "Veterinário", "Valor (R$)", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaRecentes = new JTable(tableModel);
        tabelaRecentes.setRowHeight(26);
        tabelaRecentes.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        centerPanel.add(new JScrollPane(tabelaRecentes), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel criarCard(String titulo, JLabel valorLabel, Color corDestaque) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 60), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(corDestaque);

        valorLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);

        return card;
    }

    public void recarregar() {
        List<Consulta> todas = controladorConsulta.listarTodasConsultas();
        int total = todas.size();
        int pagasCount = 0;
        double faturamento = 0.0;

        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Consulta c : todas) {
            if (c.getStatus() == StatusConsulta.PAGA) {
                pagasCount++;
                faturamento += c.getValor();
            }

            tableModel.addRow(new Object[]{
                    c.getIdConsulta(),
                    c.getDataHora().format(fmt),
                    c.getAnimal() != null ? c.getAnimal().getNome() : "N/A",
                    c.getAnimal() != null ? c.getAnimal().getEspecie() + " - " + c.getAnimal().getRaca() : "N/A",
                    c.getAnimal() != null && c.getAnimal().getTutor() != null ? c.getAnimal().getTutor().getNome() : "N/A",
                    c.getVeterinario() != null ? c.getVeterinario().getNome() : "N/A",
                    String.format("%.2f", c.getValor()),
                    c.getStatus().getDescricao()
            });
        }

        int totalAnimais = controladorCadastros.listarAnimais().size();

        lblTotalConsultas.setText(String.valueOf(total));
        lblConsultasPagas.setText(String.valueOf(pagasCount));
        lblFaturamentoTotal.setText(String.format("R$ %.2f", faturamento));
        lblTotalAnimais.setText(String.valueOf(totalAnimais));
    }
}
