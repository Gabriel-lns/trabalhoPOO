package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Tutor;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimalRepository {
    private final TutorRepository tutorRepository = new TutorRepository();

    public Animal salvar(Animal animal) {
        String sql = "INSERT INTO animais (nome, especie, raca, data_nascimento, tutor_cpf) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            stmt.setString(4, animal.getDataNascimento() != null ? animal.getDataNascimento().toString() : LocalDate.now().toString());
            stmt.setString(5, animal.getTutor() != null ? animal.getTutor().getCpf() : null);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    animal.setIdAnimal(generatedKeys.getInt(1));
                }
            }

            // Criar automaticamente o prontuário para o animal (RN05)
            criarProntuarioParaAnimal(animal.getIdAnimal());

            return animal;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar animal: " + e.getMessage(), e);
        }
    }

    private void criarProntuarioParaAnimal(int idAnimal) {
        String sql = "INSERT OR IGNORE INTO prontuarios (id_animal, data_criacao) VALUES (?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAnimal);
            stmt.setString(2, LocalDate.now().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao criar prontuário automático: " + e.getMessage());
        }
    }

    public Optional<Animal> buscarPorId(int id) {
        String sql = "SELECT id_animal, nome, especie, raca, data_nascimento, tutor_cpf FROM animais WHERE id_animal = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Tutor tutor = null;
                    String tutorCpf = rs.getString("tutor_cpf");
                    if (tutorCpf != null) {
                        tutor = tutorRepository.buscarPorCpf(tutorCpf).orElse(null);
                    }
                    LocalDate dtNasc = rs.getString("data_nascimento") != null ? LocalDate.parse(rs.getString("data_nascimento")) : LocalDate.now();
                    return Optional.of(new Animal(
                            rs.getInt("id_animal"),
                            rs.getString("nome"),
                            rs.getString("especie"),
                            rs.getString("raca"),
                            dtNasc,
                            tutor
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar animal por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Animal> listarTodos() {
        List<Animal> lista = new ArrayList<>();
        String sql = "SELECT id_animal, nome, especie, raca, data_nascimento, tutor_cpf FROM animais ORDER BY nome ASC;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tutor tutor = null;
                String tutorCpf = rs.getString("tutor_cpf");
                if (tutorCpf != null) {
                    tutor = tutorRepository.buscarPorCpf(tutorCpf).orElse(null);
                }
                LocalDate dtNasc = rs.getString("data_nascimento") != null ? LocalDate.parse(rs.getString("data_nascimento")) : LocalDate.now();
                lista.add(new Animal(
                        rs.getInt("id_animal"),
                        rs.getString("nome"),
                        rs.getString("especie"),
                        rs.getString("raca"),
                        dtNasc,
                        tutor
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar animais: " + e.getMessage(), e);
        }
        return lista;
    }
}
