package com.clinicaveterinaria.boundary;

import com.clinicaveterinaria.control.ControladorFinanceiro;
import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Pagamento;
import com.clinicaveterinaria.entity.enums.MetodoPagamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Boundary da Tela de Caixa e Faturamento.
 * Corresponde ao diagrama de sequência SD03 - Registrar Pagamento.
 * Utiliza o padrão STRATEGY para processar o método de pagamento selecionado.
 */
public class TelaCaixaPanel extends JPanel {
    private final ControladorFinanceiro controladorFinanceiro;
    private final Runnable onDataChanged;

    private JTable tabelaPendentes;
    private DefaultTableModel tableModel;

    private JLabel lblValorTotal;
    private JComboBox<MetodoPagamento> cbMetodoPagamento;
    private JTextArea txtRecibo;
    private Consulta consultaSelecionada;

    public TelaCaixaPanel(ControladorFinanceiro controladorFinanceiro, Runnable onDataChanged) {
        this.controladorFinanceiro = controladorFinanceiro;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        recarregar();
    }

    private void initComponents() {
        // Painel Superior: Consultas Realizadas Pendentes de Faturamento (RN07)
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("1. Consultas Realizadas Prontas para Pagamento (RN07 / SD03)"));
        topPanel.setPreferredSize(new Dimension(0, 200));

        String[] colunas = {"ID", "Data / Hora", "Paciente", "Tutor", "Veterinário", "Valor Devido (R$)", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaPendentes = new JTable(tableModel);
        tabelaPendentes.setRowHeight(24);
        tabelaPendentes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarConsulta();
            }
        });

        topPanel.add(new JScrollPane(tabelaPendentes), BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Painel Central: Operação de Pagamento e Emissão de Recibo
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 15));

        // Lado Esquerdo: Formulário de Liquidação
        JPanel formPagamento = new JPanel();
        formPagamento.setLayout(new BoxLayout(formPagamento, BoxLayout.Y_AXIS));
        formPagamento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("2. Processamento Financeiro (Strategy Pattern)"),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        lblValorTotal = new JLabel("R$ 0,00");
        lblValorTotal.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblValorTotal.setForeground(new Color(46, 204, 113));

        cbMetodoPagamento = new JComboBox<>(MetodoPagamento.values());
        cbMetodoPagamento.setFont(new Font("SansSerif", Font.PLAIN, 13));

        formPagamento.add(new JLabel("Valor a Pagar:"));
        formPagamento.add(lblValorTotal);
        formPagamento.add(Box.createVerticalStrut(15));

        formPagamento.add(new JLabel("Selecione o Método de Pagamento:"));
        formPagamento.add(cbMetodoPagamento);
        formPagamento.add(Box.createVerticalStrut(25));

        JButton btnPagar = new JButton("💳 Confirmar Recebimento & Liquidar");
        btnPagar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnPagar.setBackground(new Color(46, 204, 113));
        btnPagar.setForeground(Color.WHITE);
        btnPagar.addActionListener(e -> executarProcessamentoPagamento());
        formPagamento.add(btnPagar);

        centerPanel.add(formPagamento);

        // Lado Direito: Comprovante e Recibo Emitido
        JPanel reciboPanel = new JPanel(new BorderLayout(5, 5));
        reciboPanel.setBorder(BorderFactory.createTitledBorder("3. Recibo e Comprovante de Pagamento"));

        txtRecibo = new JTextArea();
        txtRecibo.setEditable(false);
        txtRecibo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtRecibo.setText("Aguardando liquidação de consulta...");
        reciboPanel.add(new JScrollPane(txtRecibo), BorderLayout.CENTER);

        centerPanel.add(reciboPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void selecionarConsulta() {
        int row = tabelaPendentes.getSelectedRow();
        if (row < 0) return;

        int idConsulta = (int) tableModel.getValueAt(row, 0);
        try {
            this.consultaSelecionada = controladorFinanceiro.buscarDadosPagamento(idConsulta);
            lblValorTotal.setText(String.format("R$ %.2f", consultaSelecionada.getValorConsulta()));
        } catch (Exception ex) {
            lblValorTotal.setText("R$ 0,00");
        }
    }

    private void executarProcessamentoPagamento() {
        if (consultaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta realizada na tabela acima.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MetodoPagamento metodo = (MetodoPagamento) cbMetodoPagamento.getSelectedItem();
        if (metodo == null) {
            JOptionPane.showMessageDialog(this, "Selecione um método de pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Executar processamento (SD03 - Mensagem 7 a 10)
            Pagamento pagamento = controladorFinanceiro.processarPagamento(consultaSelecionada.getIdConsulta(), metodo);

            // Exibir comprovante formatado
            txtRecibo.setText(pagamento.getComprovante());
            txtRecibo.setCaretPosition(0);

            JOptionPane.showMessageDialog(this,
                    "Pagamento registrado com sucesso!\n" +
                    "Consulta #" + consultaSelecionada.getIdConsulta() + " transicionou para status 'Paga'.",
                    "Pagamento Concluído", JOptionPane.INFORMATION_MESSAGE);

            consultaSelecionada = null;
            lblValorTotal.setText("R$ 0,00");
            recarregar();

            if (onDataChanged != null) onDataChanged.run();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no Pagamento", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void recarregar() {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Consulta> pendentes = controladorFinanceiro.listarConsultasPendentesPagamento();

        for (Consulta c : pendentes) {
            tableModel.addRow(new Object[]{
                    c.getIdConsulta(),
                    c.getDataHora().format(fmt),
                    c.getAnimal() != null ? c.getAnimal().getNome() : "N/A",
                    c.getAnimal() != null && c.getAnimal().getTutor() != null ? c.getAnimal().getTutor().getNome() : "N/A",
                    c.getVeterinario() != null ? c.getVeterinario().getNome() : "N/A",
                    String.format("%.2f", c.getValor()),
                    c.getStatus().getDescricao()
            });
        }
    }
}
