package com.clinicaveterinaria.boundary;

import com.clinicaveterinaria.control.ControladorCadastros;
import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Tutor;
import com.clinicaveterinaria.entity.Veterinario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaCadastrosPanel extends JPanel {
    private final ControladorCadastros controladorCadastros;
    private final Runnable onDataChanged;

    private JTabbedPane subTabbedPane;

    // Tutores
    private JTextField txtTutorCpf, txtTutorNome, txtTutorTelefone, txtTutorEndereco;
    private JTable tabelaTutores;
    private DefaultTableModel modelTutores;

    // Animais
    private JTextField txtAnimalNome, txtAnimalEspecie, txtAnimalRaca, txtAnimalIdadeAnos;
    private JComboBox<Tutor> cbAnimalTutor;
    private JTable tabelaAnimais;
    private DefaultTableModel modelAnimais;

    // Veterinários
    private JTextField txtVetCrmv, txtVetNome, txtVetEspecialidade, txtVetTelefone;
    private JTable tabelaVeterinarios;
    private DefaultTableModel modelVeterinarios;

    public TelaCadastrosPanel(ControladorCadastros controladorCadastros, Runnable onDataChanged) {
        this.controladorCadastros = controladorCadastros;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout());
        initComponents();
        recarregar();
    }

    private void initComponents() {
        subTabbedPane = new JTabbedPane();

        subTabbedPane.addTab("👤 Manter Tutores (RF01)", criarAbaTutores());
        subTabbedPane.addTab("🐾 Manter Animais (RF02 / RN01)", criarAbaAnimais());
        subTabbedPane.addTab("🩺 Manter Veterinários (RF03)", criarAbaVeterinarios());

        add(subTabbedPane, BorderLayout.CENTER);
    }

    private JPanel criarAbaTutores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Cadastro de Novo Tutor"));

        txtTutorCpf = new JTextField();
        txtTutorNome = new JTextField();
        txtTutorTelefone = new JTextField();
        txtTutorEndereco = new JTextField();

        form.add(new JLabel("CPF:"));
        form.add(txtTutorCpf);
        form.add(new JLabel("Nome Completo:"));
        form.add(txtTutorNome);
        form.add(new JLabel("Telefone / Contato:"));
        form.add(txtTutorTelefone);
        form.add(new JLabel("Endereço:"));
        form.add(txtTutorEndereco);

        JButton btnSalvar = new JButton("💾 Salvar Tutor");
        btnSalvar.addActionListener(e -> {
            try {
                controladorCadastros.cadastrarTutor(txtTutorCpf.getText(), txtTutorNome.getText(), txtTutorTelefone.getText(), txtTutorEndereco.getText());
                JOptionPane.showMessageDialog(this, "Tutor cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCamposTutor();
                recarregar();
                if (onDataChanged != null) onDataChanged.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(new JLabel(""));
        form.add(btnSalvar);

        panel.add(form, BorderLayout.NORTH);

        modelTutores = new DefaultTableModel(new String[]{"CPF", "Nome", "Telefone", "Endereço"}, 0);
        tabelaTutores = new JTable(modelTutores);
        panel.add(new JScrollPane(tabelaTutores), BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarAbaAnimais() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Cadastro de Novo Animal (Paciente)"));

        txtAnimalNome = new JTextField();
        txtAnimalEspecie = new JTextField("Canina");
        txtAnimalRaca = new JTextField("SRD");
        txtAnimalIdadeAnos = new JTextField("2");
        cbAnimalTutor = new JComboBox<>();

        form.add(new JLabel("Nome do Animal:"));
        form.add(txtAnimalNome);
        form.add(new JLabel("Espécie:"));
        form.add(txtAnimalEspecie);
        form.add(new JLabel("Raça:"));
        form.add(txtAnimalRaca);
        form.add(new JLabel("Idade Aproximada (Anos):"));
        form.add(txtAnimalIdadeAnos);
        form.add(new JLabel("Tutor Responsável (RN01):"));
        form.add(cbAnimalTutor);

        JButton btnSalvar = new JButton("🐾 Cadastrar Animal");
        btnSalvar.addActionListener(e -> {
            Tutor tutor = (Tutor) cbAnimalTutor.getSelectedItem();
            if (tutor == null) {
                JOptionPane.showMessageDialog(this, "Selecione um tutor.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int anos = Integer.parseInt(txtAnimalIdadeAnos.getText().trim());
                LocalDate dtNasc = LocalDate.now().minusYears(anos);
                controladorCadastros.cadastrarAnimal(txtAnimalNome.getText(), txtAnimalEspecie.getText(), txtAnimalRaca.getText(), dtNasc, tutor.getCpf());
                JOptionPane.showMessageDialog(this, "Animal cadastrado e prontuário aberto com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCamposAnimal();
                recarregar();
                if (onDataChanged != null) onDataChanged.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(new JLabel(""));
        form.add(btnSalvar);

        panel.add(form, BorderLayout.NORTH);

        modelAnimais = new DefaultTableModel(new String[]{"ID", "Nome", "Espécie", "Raça", "Idade", "Tutor"}, 0);
        tabelaAnimais = new JTable(modelAnimais);
        panel.add(new JScrollPane(tabelaAnimais), BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarAbaVeterinarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Cadastro de Novo Médico Veterinário"));

        txtVetCrmv = new JTextField();
        txtVetNome = new JTextField();
        txtVetEspecialidade = new JTextField("Clínica Geral");
        txtVetTelefone = new JTextField();

        form.add(new JLabel("CRMV:"));
        form.add(txtVetCrmv);
        form.add(new JLabel("Nome Completo:"));
        form.add(txtVetNome);
        form.add(new JLabel("Especialidade:"));
        form.add(txtVetEspecialidade);
        form.add(new JLabel("Telefone / Contato:"));
        form.add(txtVetTelefone);

        JButton btnSalvar = new JButton("🩺 Cadastrar Veterinário");
        btnSalvar.addActionListener(e -> {
            try {
                controladorCadastros.cadastrarVeterinario(txtVetCrmv.getText(), txtVetNome.getText(), txtVetEspecialidade.getText(), txtVetTelefone.getText());
                JOptionPane.showMessageDialog(this, "Veterinário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCamposVet();
                recarregar();
                if (onDataChanged != null) onDataChanged.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(new JLabel(""));
        form.add(btnSalvar);

        panel.add(form, BorderLayout.NORTH);

        modelVeterinarios = new DefaultTableModel(new String[]{"CRMV", "Nome", "Especialidade", "Telefone"}, 0);
        tabelaVeterinarios = new JTable(modelVeterinarios);
        panel.add(new JScrollPane(tabelaVeterinarios), BorderLayout.CENTER);

        return panel;
    }

    private void limparCamposTutor() {
        txtTutorCpf.setText("");
        txtTutorNome.setText("");
        txtTutorTelefone.setText("");
        txtTutorEndereco.setText("");
    }

    private void limparCamposAnimal() {
        txtAnimalNome.setText("");
        txtAnimalEspecie.setText("Canina");
        txtAnimalRaca.setText("SRD");
        txtAnimalIdadeAnos.setText("2");
    }

    private void limparCamposVet() {
        txtVetCrmv.setText("");
        txtVetNome.setText("");
        txtVetEspecialidade.setText("Clínica Geral");
        txtVetTelefone.setText("");
    }

    public void recarregar() {
        // Recarregar Tutores
        modelTutores.setRowCount(0);
        cbAnimalTutor.removeAllItems();
        List<Tutor> tutores = controladorCadastros.listarTutores();
        for (Tutor t : tutores) {
            modelTutores.addRow(new Object[]{t.getCpf(), t.getNome(), t.getTelefone(), t.getEndereco()});
            cbAnimalTutor.addItem(t);
        }

        // Recarregar Animais
        modelAnimais.setRowCount(0);
        for (Animal a : controladorCadastros.listarAnimais()) {
            modelAnimais.addRow(new Object[]{
                    a.getIdAnimal(),
                    a.getNome(),
                    a.getEspecie(),
                    a.getRaca(),
                    a.obterIdade() + " anos",
                    a.getTutor() != null ? a.getTutor().getNome() : "N/A"
            });
        }

        // Recarregar Veterinários
        modelVeterinarios.setRowCount(0);
        for (Veterinario v : controladorCadastros.listarVeterinarios()) {
            modelVeterinarios.addRow(new Object[]{v.getCrmv(), v.getNome(), v.getEspecialidade(), v.getTelefone()});
        }
    }
}
