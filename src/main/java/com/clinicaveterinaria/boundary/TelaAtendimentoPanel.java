package com.clinicaveterinaria.boundary;

import com.clinicaveterinaria.control.ControladorAtendimento;
import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Exame;
import com.clinicaveterinaria.entity.Prontuario;
import com.clinicaveterinaria.entity.Vacina;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Boundary da Tela de Atendimento do Consultório Veterinário.
 * Corresponde ao diagrama de sequência SD02 - Realizar Consulta e Prontuário.
 */
public class TelaAtendimentoPanel extends JPanel {
    private final ControladorAtendimento controladorAtendimento;
    private final Runnable onDataChanged;

    private JTable tabelaConsultas;
    private DefaultTableModel tableModel;

    private JLabel lblPacienteInfo;
    private JTextArea txtHistoricoProntuario;
    private JTextArea txtDiagnostico;

    private Consulta consultaSelecionada;

    public TelaAtendimentoPanel(ControladorAtendimento controladorAtendimento, Runnable onDataChanged) {
        this.controladorAtendimento = controladorAtendimento;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        recarregar();
    }

    private void initComponents() {
        // Painel Superior: Tabela de Consultas para Atender
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("1. Selecione a Consulta para Iniciar Atendimento"));
        topPanel.setPreferredSize(new Dimension(0, 180));

        String[] colunas = {"ID", "Data / Hora", "Paciente (Animal)", "Tutor", "Veterinário", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaConsultas = new JTable(tableModel);
        tabelaConsultas.setRowHeight(24);
        tabelaConsultas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarConsultaSelecionada();
            }
        });

        topPanel.add(new JScrollPane(tabelaConsultas), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnIniciar = new JButton("▶️ Iniciar Atendimento Clínico");
        btnIniciar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnIniciar.addActionListener(e -> executarInicioAtendimento());
        btnPanel.add(btnIniciar);

        topPanel.add(btnPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Painel Central: Prontuário e Prescrição
        JPanel mainAtendimentoPanel = new JPanel(new GridLayout(1, 2, 15, 15));

        // Coluna 1: Dados do Paciente e Histórico do Prontuário
        JPanel prontuarioPanel = new JPanel(new BorderLayout(8, 8));
        prontuarioPanel.setBorder(BorderFactory.createTitledBorder("2. Prontuário Eletrônico do Paciente (RN03 / RN08)"));

        lblPacienteInfo = new JLabel("Selecione uma consulta acima para carregar o histórico clínico.");
        lblPacienteInfo.setFont(new Font("SansSerif", Font.BOLD, 12));
        prontuarioPanel.add(lblPacienteInfo, BorderLayout.NORTH);

        txtHistoricoProntuario = new JTextArea();
        txtHistoricoProntuario.setEditable(false);
        txtHistoricoProntuario.setFont(new Font("Monospaced", Font.PLAIN, 12));
        prontuarioPanel.add(new JScrollPane(txtHistoricoProntuario), BorderLayout.CENTER);

        mainAtendimentoPanel.add(prontuarioPanel);

        // Coluna 2: Anamnese, Exames, Vacinas e Finalização
        JPanel acoesPanel = new JPanel(new BorderLayout(8, 8));
        acoesPanel.setBorder(BorderFactory.createTitledBorder("3. Anamnese, Procedimentos e Fechamento"));

        JPanel formPrescricao = new JPanel(new BorderLayout(5, 5));
        formPrescricao.add(new JLabel("Diagnóstico / Prescrição Médica da Consulta:"), BorderLayout.NORTH);

        txtDiagnostico = new JTextArea();
        txtDiagnostico.setLineWrap(true);
        txtDiagnostico.setWrapStyleWord(true);
        formPrescricao.add(new JScrollPane(txtDiagnostico), BorderLayout.CENTER);

        acoesPanel.add(formPrescricao, BorderLayout.CENTER);

        // Botões de Procedimentos e Finalização
        JPanel botoesProcedimentos = new JPanel(new GridLayout(3, 1, 6, 6));

        JButton btnSolicitarExame = new JButton("🧪 Solicitar / Anexar Exame");
        btnSolicitarExame.addActionListener(e -> dialogSolicitarExame());
        botoesProcedimentos.add(btnSolicitarExame);

        JButton btnAplicarVacina = new JButton("💉 Registrar Aplicação de Vacina");
        btnAplicarVacina.addActionListener(e -> dialogAplicarVacina());
        botoesProcedimentos.add(btnAplicarVacina);

        JButton btnFinalizar = new JButton("🩺 Finalizar Atendimento e Liberar para Caixa");
        btnFinalizar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnFinalizar.setBackground(new Color(52, 152, 219));
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.addActionListener(e -> executarFinalizacao());
        botoesProcedimentos.add(btnFinalizar);

        acoesPanel.add(botoesProcedimentos, BorderLayout.SOUTH);

        mainAtendimentoPanel.add(acoesPanel);
        add(mainAtendimentoPanel, BorderLayout.CENTER);
    }

    private void carregarConsultaSelecionada() {
        int row = tabelaConsultas.getSelectedRow();
        if (row < 0) return;

        int idConsulta = (int) tableModel.getValueAt(row, 0);
        List<Consulta> lista = controladorAtendimento.listarConsultasParaAtendimento();
        this.consultaSelecionada = lista.stream().filter(c -> c.getIdConsulta() == idConsulta).findFirst().orElse(null);

        if (consultaSelecionada != null && consultaSelecionada.getAnimal() != null) {
            Animal a = consultaSelecionada.getAnimal();
            lblPacienteInfo.setText(String.format("<html>Paciente: <b>%s</b> | Espécie: %s | Idade: %d anos | Tutor: %s</html>",
                    a.getNome(), a.getEspecie(), a.obterIdade(), a.getTutor() != null ? a.getTutor().getNome() : "N/A"));

            try {
                Prontuario p = controladorAtendimento.buscarHistoricoAnimal(a.getIdAnimal());
                StringBuilder sb = new StringBuilder();
                sb.append("=== HISTÓRICO CLÍNICO IMUTÁVEL (PRONTUÁRIO #").append(p.getIdProntuario()).append(") ===\n\n");
                for (String reg : p.consultarHistorico()) {
                    sb.append(reg).append("\n\n");
                }

                // Listar Exames e Vacinas
                List<Exame> exames = controladorAtendimento.listarExamesDoProntuario(p.getIdProntuario());
                if (!exames.isEmpty()) {
                    sb.append("--- EXAMES REALIZADOS ---\n");
                    for (Exame ex : exames) sb.append("• ").append(ex).append("\n");
                    sb.append("\n");
                }

                List<Vacina> vacinas = controladorAtendimento.listarVacinasDoProntuario(p.getIdProntuario());
                if (!vacinas.isEmpty()) {
                    sb.append("--- CARTEIRA DE VACINAÇÃO ---\n");
                    for (Vacina vac : vacinas) sb.append("• ").append(vac).append("\n");
                    sb.append("\n");
                }

                txtHistoricoProntuario.setText(sb.toString());
                txtHistoricoProntuario.setCaretPosition(0);

            } catch (Exception ex) {
                txtHistoricoProntuario.setText("Não foi possível carregar o prontuário: " + ex.getMessage());
            }
        }
    }

    private void executarInicioAtendimento() {
        if (consultaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta na tabela acima.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            controladorAtendimento.iniciarAtendimento(consultaSelecionada.getIdConsulta());
            JOptionPane.showMessageDialog(this, "Atendimento da Consulta #" + consultaSelecionada.getIdConsulta() + " iniciado com sucesso!", "Consultório", JOptionPane.INFORMATION_MESSAGE);
            recarregar();
            if (onDataChanged != null) onDataChanged.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void dialogSolicitarExame() {
        if (consultaSelecionada == null || consultaSelecionada.getAnimal() == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta ativa primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipo = JOptionPane.showInputDialog(this, "Informe o tipo do exame (Ex: Hemograma Completo, Raio-X):", "Solicitação de Exame", JOptionPane.QUESTION_MESSAGE);
        if (tipo != null && !tipo.trim().isEmpty()) {
            try {
                Prontuario p = controladorAtendimento.buscarHistoricoAnimal(consultaSelecionada.getAnimal().getIdAnimal());
                controladorAtendimento.registrarExame(consultaSelecionada.getIdConsulta(), p.getIdProntuario(), tipo, "Laudo em processamento");
                JOptionPane.showMessageDialog(this, "Exame registrado com sucesso no prontuário!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarConsultaSelecionada();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogAplicarVacina() {
        if (consultaSelecionada == null || consultaSelecionada.getAnimal() == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta ativa primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = JOptionPane.showInputDialog(this, "Informe o nome da Vacina (Ex: Antirrábica, V10):", "Aplicação de Vacina", JOptionPane.QUESTION_MESSAGE);
        if (nome != null && !nome.trim().isEmpty()) {
            try {
                Prontuario p = controladorAtendimento.buscarHistoricoAnimal(consultaSelecionada.getAnimal().getIdAnimal());
                controladorAtendimento.registrarVacina(consultaSelecionada.getIdConsulta(), p.getIdProntuario(), nome, LocalDate.now().plusYears(1));
                JOptionPane.showMessageDialog(this, "Vacina registrada na carteira do paciente com reforço para daqui a 1 ano!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarConsultaSelecionada();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void executarFinalizacao() {
        if (consultaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta para finalizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String diagnostico = txtDiagnostico.getText().trim();
        if (diagnostico.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o diagnóstico/prescrição antes de finalizar a consulta.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            controladorAtendimento.finalizarConsulta(consultaSelecionada.getIdConsulta(), diagnostico);
            JOptionPane.showMessageDialog(this, "Consulta #" + consultaSelecionada.getIdConsulta() + " finalizada com sucesso!\n" +
                    "A consulta transicionou para status 'Realizada' e está liberada no Caixa para pagamento.", "Atendimento Concluído", JOptionPane.INFORMATION_MESSAGE);

            txtDiagnostico.setText("");
            recarregar();
            if (onDataChanged != null) onDataChanged.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao finalizar", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void recarregar() {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Consulta> lista = controladorAtendimento.listarConsultasParaAtendimento();

        for (Consulta c : lista) {
            tableModel.addRow(new Object[]{
                    c.getIdConsulta(),
                    c.getDataHora().format(fmt),
                    c.getAnimal() != null ? c.getAnimal().getNome() + " (" + c.getAnimal().getEspecie() + ")" : "N/A",
                    c.getAnimal() != null && c.getAnimal().getTutor() != null ? c.getAnimal().getTutor().getNome() : "N/A",
                    c.getVeterinario() != null ? c.getVeterinario().getNome() : "N/A",
                    c.getStatus().getDescricao()
            });
        }
    }
}
