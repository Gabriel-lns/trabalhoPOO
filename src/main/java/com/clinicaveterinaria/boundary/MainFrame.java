package com.clinicaveterinaria.boundary;

import com.clinicaveterinaria.control.ControladorAtendimento;
import com.clinicaveterinaria.control.ControladorCadastros;
import com.clinicaveterinaria.control.ControladorConsulta;
import com.clinicaveterinaria.control.ControladorFinanceiro;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Janela Principal da Aplicação com tema moderno FlatLaf.
 * Boundary central que abriga os módulos do sistema.
 */
public class MainFrame extends JFrame {
    private final ControladorConsulta controladorConsulta;
    private final ControladorAtendimento controladorAtendimento;
    private final ControladorFinanceiro controladorFinanceiro;
    private final ControladorCadastros controladorCadastros;

    private JTabbedPane tabbedPane;
    private TelaAgendamentoPanel telaAgendamento;
    private TelaAtendimentoPanel telaAtendimento;
    private TelaCaixaPanel telaCaixa;
    private TelaCadastrosPanel telaCadastros;
    private TelaDashboardPanel telaDashboard;

    public MainFrame() {
        this.controladorConsulta = new ControladorConsulta();
        this.controladorAtendimento = new ControladorAtendimento();
        this.controladorFinanceiro = new ControladorFinanceiro();
        this.controladorCadastros = new ControladorCadastros();

        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Clínica Veterinária - Emily & Gabriel (POO)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // Barra de Ferramentas Superior
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel titleLabel = new JLabel("CLÍNICA VETERINÁRIA - GESTÃO INTEGRADA");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        toolBar.add(titleLabel);

        toolBar.add(Box.createHorizontalGlue());

        JButton btnAtualizar = new JButton("🔄 Atualizar Dados");
        btnAtualizar.addActionListener(e -> recarregarTodasTelas());
        toolBar.add(btnAtualizar);

        toolBar.add(Box.createHorizontalStrut(10));

        JToggleButton btnTheme = new JToggleButton("🌓 Alternar Tema");
        btnTheme.addActionListener(e -> alternarTema(btnTheme.isSelected()));
        toolBar.add(btnTheme);

        add(toolBar, BorderLayout.NORTH);

        // Painel de Abas Moderno
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        // Inicializar painéis
        telaDashboard = new TelaDashboardPanel(controladorConsulta, controladorFinanceiro, controladorCadastros);
        telaAgendamento = new TelaAgendamentoPanel(controladorConsulta, controladorCadastros, this::recarregarTodasTelas);
        telaAtendimento = new TelaAtendimentoPanel(controladorAtendimento, this::recarregarTodasTelas);
        telaCaixa = new TelaCaixaPanel(controladorFinanceiro, this::recarregarTodasTelas);
        telaCadastros = new TelaCadastrosPanel(controladorCadastros, this::recarregarTodasTelas);

        tabbedPane.addTab("📊 Dashboard", telaDashboard);
        tabbedPane.addTab("📅 1. Agendamento (SD01)", telaAgendamento);
        tabbedPane.addTab("🩺 2. Consultório & Prontuário (SD02)", telaAtendimento);
        tabbedPane.addTab("💳 3. Caixa & Pagamentos (SD03)", telaCaixa);
        tabbedPane.addTab("👥 Cadastros Gerais", telaCadastros);

        // Listener para atualizar a aba quando for selecionada
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0 -> telaDashboard.recarregar();
                case 1 -> telaAgendamento.recarregar();
                case 2 -> telaAtendimento.recarregar();
                case 3 -> telaCaixa.recarregar();
                case 4 -> telaCadastros.recarregar();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);

        // Barra de Status Inferior
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        JLabel statusLabel = new JLabel("Projeto de POO | Arquitetura BCE | Design Patterns: State, Strategy, Factory Method | SQLite Ativo");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);

        JLabel autoriaLabel = new JLabel("Desenvolvido por: Emily Silva & Gabriel Nunes");
        autoriaLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(autoriaLabel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);
    }

    public void recarregarTodasTelas() {
        telaDashboard.recarregar();
        telaAgendamento.recarregar();
        telaAtendimento.recarregar();
        telaCaixa.recarregar();
        telaCadastros.recarregar();
    }

    private void alternarTema(boolean dark) {
        try {
            if (dark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
