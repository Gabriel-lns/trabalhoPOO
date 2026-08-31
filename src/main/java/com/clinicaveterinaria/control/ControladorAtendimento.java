package com.clinicaveterinaria.control;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Exame;
import com.clinicaveterinaria.entity.Prontuario;
import com.clinicaveterinaria.entity.Vacina;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador de Atendimento Clínico e Consultório Médico (BCE).
 * Aplica Inversão de Dependência (DIP) e prontuário imutável (RN03/RN05/RN08).
 */
public class ControladorAtendimento {
    private final ConsultaRepository consultaRepository;
    private final ProntuarioRepository prontuarioRepository;
    private final ExameRepository exameRepository;
    private final VacinaRepository vacinaRepository;

    public ControladorAtendimento() {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        this.consultaRepository = factory.getConsultaRepository();
        this.prontuarioRepository = factory.getProntuarioRepository();
        this.exameRepository = factory.getExameRepository();
        this.vacinaRepository = factory.getVacinaRepository();
    }

    public ControladorAtendimento(ConsultaRepository consultaRepository, ProntuarioRepository prontuarioRepository,
                                  ExameRepository exameRepository, VacinaRepository vacinaRepository) {
        this.consultaRepository = consultaRepository;
        this.prontuarioRepository = prontuarioRepository;
        this.exameRepository = exameRepository;
        this.vacinaRepository = vacinaRepository;
    }

    /**
     * Inicia o atendimento clínico da consulta (SD02 - Mensagem 2).
     */
    public Consulta iniciarAtendimento(int idConsulta) {
        Consulta consulta = consultaRepository.buscarPorId(idConsulta)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + idConsulta));

        consulta.iniciar();
        return consultaRepository.atualizar(consulta);
    }

    /**
     * Busca o prontuário eletrônico completo do paciente (SD02 - Mensagem 4).
     */
    public Prontuario buscarHistoricoAnimal(int idAnimal) {
        Consulta dummy = new Consulta();
        return prontuarioRepository.buscarPorAnimalId(idAnimal, null)
                .orElseThrow(() -> new IllegalArgumentException("Prontuário não localizado para o animal ID: " + idAnimal));
    }

    /**
     * Registra solicitação de exame no atendimento.
     */
    public Exame registrarExame(int idConsulta, int idProntuario, String tipo, String resultadoInicial) {
        Exame exame = new Exame(0, tipo, LocalDate.now(), resultadoInicial, idConsulta, idProntuario);
        return exameRepository.salvar(exame);
    }

    /**
     * Registra aplicação de vacina com reforço na carteira.
     */
    public Vacina registrarVacina(int idConsulta, int idProntuario, String nome, LocalDate proximaDose) {
        Vacina vacina = new Vacina(0, nome, LocalDate.now(), proximaDose, idConsulta, idProntuario);
        return vacinaRepository.salvar(vacina);
    }

    /**
     * Finaliza o atendimento clínico gravando a evolução indelével no prontuário (SD02 - Mensagem 8 a 10).
     */
    public Consulta finalizarConsulta(int idConsulta, String diagnostico) {
        Consulta consulta = consultaRepository.buscarPorId(idConsulta)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + idConsulta));

        // 1. Transicionar status para Realizada via State Pattern
        consulta.finalizar(diagnostico);
        consultaRepository.atualizar(consulta);

        // 2. Gravar registro permanente no prontuário eletrônico (RN03 / RN05 / RN08)
        if (consulta.getAnimal() != null) {
            String carimbo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String registro = String.format("[%s] Consulta realizada pelo(a) %s. Diagnóstico: %s",
                    carimbo,
                    consulta.getVeterinario() != null ? consulta.getVeterinario().getNome() : "Veterinário Responsável",
                    diagnostico);

            prontuarioRepository.buscarPorAnimalId(consulta.getAnimal().getIdAnimal(), consulta.getAnimal())
                    .ifPresent(p -> prontuarioRepository.adicionarRegistroClinico(p.getIdProntuario(), registro));
        }

        return consulta;
    }

    public List<Consulta> listarConsultasParaAtendimento() {
        List<Consulta> lista = consultaRepository.listarPorStatus(StatusConsulta.AGENDADA);
        lista.addAll(consultaRepository.listarPorStatus(StatusConsulta.EM_ANDAMENTO));
        return lista;
    }

    public List<Exame> listarExamesDoProntuario(int idProntuario) {
        return exameRepository.listarPorProntuario(idProntuario);
    }

    public List<Vacina> listarVacinasDoProntuario(int idProntuario) {
        return vacinaRepository.listarPorProntuario(idProntuario);
    }
}
