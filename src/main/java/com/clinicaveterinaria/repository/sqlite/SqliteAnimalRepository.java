package com.clinicaveterinaria.repository.sqlite;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Prontuario;
import com.clinicaveterinaria.entity.Tutor;
import com.clinicaveterinaria.repository.AnimalRepository;
import com.clinicaveterinaria.repository.DatabaseManager;
import com.clinicaveterinaria.repository.ProntuarioRepository;
import com.clinicaveterinaria.repository.TutorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteAnimalRepository implements AnimalRepository {
    private final TutorRepository tutorRepository;
    private final ProntuarioRepository prontuarioRepository;

    public SqliteAnimalRepository() {
        this.tutorRepository = new SqliteTutorRepository();
        this.prontuarioRepository = new SqliteProntuarioRepository();
    }

    public SqliteAnimalRepository(TutorRepository tutorRepository, ProntuarioRepository prontuarioRepository) {
        this.tutorRepository = tutorRepository;
        this.prontuarioRepository = prontuarioRepository;
    }

    @Override
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

            // Criar prontuário eletrônico automaticamente
            if (animal.getIdAnimal() > 0) {
                Prontuario prontuario = new Prontuario(0, LocalDate.now(), animal);
                prontuarioRepository.salvar(prontuario);
                animal.setProntuario(prontuario);
            }

            return animal;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar animal: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Animal> buscarPorId(int idAnimal) {
        String sql = "SELECT id_animal, nome, especie, raca, data_nascimento, tutor_cpf FROM animais WHERE id_animal = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAnimal);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarAnimal(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar animal por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Animal> listarTodos() {
        List<Animal> lista = new ArrayList<>();
        String sql = "SELECT id_animal, nome, especie, raca, data_nascimento, tutor_cpf FROM animais ORDER BY nome ASC;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montarAnimal(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar animais: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Animal> listarPorTutor(String tutorCpf) {
        List<Animal> lista = new ArrayList<>();
        String sql = "SELECT id_animal, nome, especie, raca, data_nascimento, tutor_cpf FROM animais WHERE tutor_cpf = ? ORDER BY nome ASC;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tutorCpf);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarAnimal(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar animais por tutor: " + e.getMessage(), e);
        }
        return lista;
    }

    private Animal montarAnimal(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_animal");
        String nome = rs.getString("nome");
        String especie = rs.getString("especie");
        String raca = rs.getString("raca");
        String dtStr = rs.getString("data_nascimento");
        LocalDate dtNasc = (dtStr != null && !dtStr.isEmpty()) ? LocalDate.parse(dtStr) : LocalDate.now();
        String tutorCpf = rs.getString("tutor_cpf");

        Tutor tutor = null;
        if (tutorCpf != null && !tutorCpf.isEmpty()) {
            tutor = tutorRepository.buscarPorCpf(tutorCpf).orElse(null);
        }

        Animal animal = new Animal(id, nome, especie, raca, dtNasc, tutor);
        prontuarioRepository.buscarPorAnimalId(id, animal).ifPresent(animal::setProntuario);
        return animal;
    }
}
