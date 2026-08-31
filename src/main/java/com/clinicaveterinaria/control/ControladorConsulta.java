package com.clinicaveterinaria.control;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Veterinario;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.repository.AnimalRepository;
import com.clinicaveterinaria.repository.ConsultaRepository;
import com.clinicaveterinaria.repository.RepositoryFactory;
import com.clinicaveterinaria.repository.VeterinarioRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador de Agendamento e Recepção (BCE).
 * Aplica Inversão de Dependência (DIP) dependendo exclusivamente de interfaces de repositório.
 */
public class ControladorConsulta {
    private final ConsultaRepository consultaRepository;
    private final AnimalRepository animalRepository;
    private final VeterinarioRepository veterinarioRepository;

    public ControladorConsulta() {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        this.consultaRepository = factory.getConsultaRepository();
        this.animalRepository = factory.getAnimalRepository();
        this.veterinarioRepository = factory.getVeterinarioRepository();
    }

    public ControladorConsulta(ConsultaRepository consultaRepository, AnimalRepository animalRepository, VeterinarioRepository veterinarioRepository) {
        this.consultaRepository = consultaRepository;
        this.animalRepository = animalRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    /**
     * Solicita um novo agendamento validando regras de negócio (SD01 - Mensagem 2).
     */
    public Consulta solicitarAgendamento(String crmvVeterinario, int idAnimal, LocalDateTime dataHora, double valor) {
        // 1. Validar existência do Animal (RN04)
        Animal animal = animalRepository.buscarPorId(idAnimal)
                .orElseThrow(() -> new IllegalArgumentException("RN04: Animal com ID " + idAnimal + " não foi encontrado no sistema."));

        // 2. Validar existência do Veterinário (RN06)
        Veterinario vet = veterinarioRepository.buscarPorCrmv(crmvVeterinario)
                .orElseThrow(() -> new IllegalArgumentException("RN06: Veterinário com CRMV " + crmvVeterinario + " não encontrado."));

        // 3. Validar Conflito de Horário (RN09 / SD01 - Mensagem 5)
        validarConflitoDeHorario(crmvVeterinario, dataHora);

        // 4. Instanciar Consulta via Static Factory Method (Creator Pattern)
        Consulta novaConsulta = Consulta.criarAgendamento(animal, vet, dataHora, valor);

        // 5. Salvar na persistência
        return consultaRepository.salvar(novaConsulta);
    }

    /**
     * Validação interna de conflito de agenda médica (RN09).
     */
    public void validarConflitoDeHorario(String crmvVeterinario, LocalDateTime dataHora) {
        LocalDateTime inicioJanela = dataHora.minusMinutes(29);
        LocalDateTime fimJanela = dataHora.plusMinutes(29);

        List<Consulta> conflitos = consultaRepository.buscarPorVeterinarioEPeriodo(crmvVeterinario, inicioJanela, fimJanela);
        if (!conflitos.isEmpty()) {
            throw new IllegalStateException("RN09: Conflito de agenda! O veterinário já possui uma consulta agendada às " +
                    conflitos.get(0).getDataHora().toLocalTime() + " neste mesmo dia.");
        }
    }

    public void cancelarAgendamento(int idConsulta) {
        Consulta consulta = consultaRepository.buscarPorId(idConsulta)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + idConsulta));

        consulta.cancelar();
        consultaRepository.atualizar(consulta);
    }

    public List<Consulta> listarTodasConsultas() {
        return consultaRepository.listarTodas();
    }

    public List<Consulta> listarConsultasAgendadas() {
        return consultaRepository.listarPorStatus(StatusConsulta.AGENDADA);
    }
}
