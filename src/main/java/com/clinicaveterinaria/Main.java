package com.clinicaveterinaria;

import com.clinicaveterinaria.boundary.MainFrame;
import com.clinicaveterinaria.repository.DatabaseManager;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

/**
 * Ponto de entrada da aplicação de Clínica Veterinária.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Configurar Tema Moderno FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Aviso: Look and feel FlatLaf não pode ser carregado: " + e.getMessage());
        }

        // 2. Inicializar Banco de Dados Relacional SQLite
        System.out.println("Inicializando banco de dados SQLite...");
        DatabaseManager.inicializarBanco();

        // 3. Inicializar Interface Gráfica na Thread do Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
