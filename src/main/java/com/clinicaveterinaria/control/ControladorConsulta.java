package com.clinicaveterinaria.control;

import com.clinicaveterinaria.entity.Animal;
import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Veterinario;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.repository.AnimalRepository;
import com.clinicaveterinaria.repository.ConsultaRepository;
import com.clinicaveterinaria.repository.VeterinarioRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador de Agendamento de Consultas.
 * Mapeado diretamente do diagrama de sequência SD01 - Agendar Consulta.
 */
public class ControladorConsulta {
    private final ConsultaRepository consultaRepository;
    private final AnimalRepository animalRepository;
    private final VeterinarioRepository veterinarioRepository;

    public ControladorConsulta() {
        this.consultaRepository = new ConsultaRepository();
        this.animalRepository = new AnimalRepository();
        this.veterinarioRepository = new VeterinarioRepository();
    }

    public ControladorConsulta(ConsultaRepository consultaRepository, AnimalRepository animalRepository, VeterinarioRepository veterinarioRepository) {
        this.consultaRepository = consultaRepository;
        this.animalRepository = animalRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    /**
     * Valida conflito de agenda do veterinário no horário solicitado (RN09 / SD01).
     */
    public boolean validarConflitoDeHorario(String crmvVet, LocalDateTime dataHora) {
        List<Consulta> conflitos = consultaRepository.buscarConsultasPorVeterinarioEData(crmvVet, dataHora);
        return !conflitos.isEmpty();
    }

    /**
     * Executa a solicitação de agendamento validando regras de negócio (RN04, RN06, RN09).
     */
    public Consulta solicitarAgendamento(String crmvVet, int idAnimal, LocalDateTime dataHora, double valor) {
        // RN04: Animal deve estar cadastrado
        Animal animal = animalRepository.buscarPorId(idAnimal)
                .orElseThrow(() -> new IllegalArgumentException("RN04: Animal com ID " + idAnimal + " não foi encontrado no sistema."));

        // RN06: Veterinário deve estar cadastrado
        Veterinario vet = veterinarioRepository.buscarPorCrmv(crmvVet)
                .orElseThrow(() -> new IllegalArgumentException("RN06: Veterinário com CRMV " + crmvVet + " não foi encontrado."));

        // RN09: Validação de conflito de agenda
        if (validarConflitoDeHorario(crmvVet, dataHora)) {
            throw new IllegalStateException("RN09: Conflito de Horário! O veterinário " + vet.getNome() + " já possui consulta marcada próximo a este horário.");
        }

        // Criar e salvar nova consulta
        Consulta novaConsulta = new Consulta(0, dataHora, valor, animal, vet);
        novaConsulta.setStatus(StatusConsulta.AGENDADA);

        return consultaRepository.salvar(novaConsulta);
    }

    public void cancelarAgendamento(int idConsulta) {
        Consulta consulta = consultaRepository.buscarPorId(idConsulta)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + idConsulta));

        consulta.cancelar();
        consultaRepository.atualizar(consulta);
    }

    public List<Consulta> listarConsultasAgendadas() {
        return consultaRepository.listarPorStatus(StatusConsulta.AGENDADA);
    }

    public List<Consulta> listarTodasConsultas() {
        return consultaRepository.listarTodos();
    }
}
