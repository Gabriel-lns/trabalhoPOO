package com.clinicaveterinaria.boundary;

import com.clinicaveterinaria.control.ControladorCadastros;
import com.clinicaveterinaria.control.ControladorConsulta;
import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Veterinario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Boundary da Tela de Agendamento.
 * Corresponde ao diagrama de sequência SD01 - Agendar Consulta.
 */
public class TelaAgendamentoPanel extends JPanel {
    private final ControladorConsulta controladorConsulta;
    private final ControladorCadastros controladorCadastros;
    private final Runnable onDataChanged;

    private JComboBox<Animal> cbAnimal;
    private JComboBox<Veterinario> cbVeterinario;
    private JSpinner spinnerDataHora;
    private JTextField txtValor;
    private JTable tabelaAgendadas;
    private DefaultTableModel tableModel;

    public TelaAgendamentoPanel(ControladorConsulta controladorConsulta, ControladorCadastros controladorCadastros, Runnable onDataChanged) {
        this.controladorConsulta = controladorConsulta;
        this.controladorCadastros = controladorCadastros;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        recarregar();
    }

    private void initComponents() {
        // Painel Lateral Esquerdo: Formulário de Agendamento
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Novo Agendamento de Consulta (SD01)"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.setPreferredSize(new Dimension(380, 0));

        cbAnimal = new JComboBox<>();
        cbVeterinario = new JComboBox<>();

        SpinnerDateModel dateModel = new SpinnerDateModel();
        spinnerDataHora = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerDataHora, "dd/MM/yyyy HH:mm");
        spinnerDataHora.setEditor(editor);

        txtValor = new JTextField("150.00");

        formPanel.add(new JLabel("Selecione o Animal (Paciente):"));
        formPanel.add(cbAnimal);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(new JLabel("Selecione o Veterinário:"));
        formPanel.add(cbVeterinario);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(new JLabel("Data e Hora da Consulta:"));
        formPanel.add(spinnerDataHora);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(new JLabel("Valor da Consulta (R$):"));
        formPanel.add(txtValor);
        formPanel.add(Box.createVerticalStrut(20));

        JButton btnAgendar = new JButton("📅 Confirmar Agendamento");
        btnAgendar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAgendar.setBackground(new Color(46, 204, 113));
        btnAgendar.setForeground(Color.WHITE);
        btnAgendar.addActionListener(e -> executarAgendamento());
        formPanel.add(btnAgendar);

        add(formPanel, BorderLayout.WEST);

        // Painel Central: Lista de Consultas Agendadas
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Consultas Agendadas na Recepção"));

        String[] colunas = {"ID", "Data / Hora", "Paciente", "Tutor", "Veterinário", "Valor (R$)", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaAgendadas = new JTable(tableModel);
        tabelaAgendadas.setRowHeight(24);
        centerPanel.add(new JScrollPane(tabelaAgendadas), BorderLayout.CENTER);

        // Barra inferior com ações
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("❌ Cancelar Agendamento Selecionado");
        btnCancelar.addActionListener(e -> cancelarSelecionada());
        bottomPanel.add(btnCancelar);

        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void executarAgendamento() {
        Animal animal = (Animal) cbAnimal.getSelectedItem();
        Veterinario vet = (Veterinario) cbVeterinario.getSelectedItem();

        if (animal == null || vet == null) {
            JOptionPane.showMessageDialog(this, "Selecione um animal e um veterinário válidos.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
            java.util.Date utilDate = (java.util.Date) spinnerDataHora.getValue();
            LocalDateTime ldt = utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // Chamada do Controlador (SD01 - Mensagem 2)
            Consulta c = controladorConsulta.solicitarAgendamento(vet.getCrmv(), animal.getIdAnimal(), ldt, valor);

            JOptionPane.showMessageDialog(this, "Consulta #" + c.getIdConsulta() + " agendada com sucesso para " +
                    animal.getNome() + " com " + vet.getNome() + "!", "Agendamento Confirmado", JOptionPane.INFORMATION_MESSAGE);

            recarregar();
            if (onDataChanged != null) onDataChanged.run();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor da consulta inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Alerta de Negócio (RN)", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancelarSelecionada() {
        int row = tabelaAgendadas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta agendada na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idConsulta = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente cancelar a consulta #" + idConsulta + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controladorConsulta.cancelarAgendamento(idConsulta);
                JOptionPane.showMessageDialog(this, "Consulta cancelada com sucesso.", "Cancelamento", JOptionPane.INFORMATION_MESSAGE);
                recarregar();
                if (onDataChanged != null) onDataChanged.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void recarregar() {
        // Atualizar Combos
        cbAnimal.removeAllItems();
        for (Animal a : controladorCadastros.listarAnimais()) {
            cbAnimal.addItem(a);
        }

        cbVeterinario.removeAllItems();
        for (Veterinario v : controladorCadastros.listarVeterinarios()) {
            cbVeterinario.addItem(v);
        }

        // Atualizar Tabela de Agendadas
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Consulta> agendadas = controladorConsulta.listarConsultasAgendadas();

        for (Consulta c : agendadas) {
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
