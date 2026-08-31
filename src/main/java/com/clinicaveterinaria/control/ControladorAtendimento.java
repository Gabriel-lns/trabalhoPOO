package com.clinicaveterinaria.control;

import com.clinicaveterinaria.entity.Consulta;
import com.clinicaveterinaria.entity.Exame;
import com.clinicaveterinaria.entity.Prontuario;
import com.clinicaveterinaria.entity.Vacina;
import com.clinicaveterinaria.entity.enums.StatusConsulta;
import com.clinicaveterinaria.repository.ConsultaRepository;
import com.clinicaveterinaria.repository.ExameRepository;
import com.clinicaveterinaria.repository.ProntuarioRepository;
import com.clinicaveterinaria.repository.VacinaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador do Consultório / Atendimento Médico.
 * Mapeado diretamente do diagrama de sequência SD02 - Realizar Consulta.
 */
public class ControladorAtendimento {
    private final ConsultaRepository consultaRepository;
    private final ProntuarioRepository prontuarioRepository;
    private final ExameRepository exameRepository;
    private final VacinaRepository vacinaRepository;

    public ControladorAtendimento() {
        this.consultaRepository = new ConsultaRepository();
        this.prontuarioRepository = new ProntuarioRepository();
        this.exameRepository = new ExameRepository();
        this.vacinaRepository = new VacinaRepository();
    }

    public ControladorAtendimento(ConsultaRepository consultaRepository, ProntuarioRepository prontuarioRepository,
                                  ExameRepository exameRepository, VacinaRepository vacinaRepository) {
        this.consultaRepository = consultaRepository;
        this.prontuarioRepository = prontuarioRepository;
        this.exameRepository = exameRepository;
        this.vacinaRepository = vacinaRepository;
    }

    /**
     * Inicia a consulta selecionada pelo veterinário (SD02 - Mensagem 2).
     */
    public Consulta iniciarAtendimento(int idConsulta) {
        Consulta consulta = consultaRepository.buscarPorId(idConsulta)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + idConsulta));

        consulta.iniciar();
        consultaRepository.atualizar(consulta);
        return consulta;
    }

    /**
     * Recupera o prontuário e histórico pregressos do animal (SD02 - Mensagem 5).
     */
    public Prontuario buscarHistoricoAnimal(int idAnimal) {
        return prontuarioRepository.buscarPorAnimalId(idAnimal, null)
                .orElseThrow(() -> new IllegalArgumentException("Prontuário do animal não localizado: " + idAnimal));
    }

    /**
     * Registra solicitação de exame vinculado à consulta e prontuário.
     */
    public Exame registrarExame(int idConsulta, int idProntuario, String tipo, String resultado) {
        Exame exame = new Exame(0, tipo, LocalDate.now(), resultado, idConsulta, idProntuario);
        return exameRepository.salvar(exame);
    }

    /**
     * Registra aplicação de vacina vinculada à consulta e prontuário.
     */
    public Vacina registrarVacina(int idConsulta, int idProntuario, String nome, LocalDate proximaDose) {
        Vacina vacina = new Vacina(0, nome, LocalDate.now(), proximaDose, idConsulta, idProntuario);
        return vacinaRepository.salvar(vacina);
    }

    /**
     * Finaliza o atendimento médico, gravando no prontuário a evolução clínica imutável (RN03, RN05, RN08 / SD02).
     */
    public void finalizarConsulta(int idConsulta, String dadosInspecao) {
        Consulta consulta = consultaRepository.buscarPorId(idConsulta)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + idConsulta));

        // 1. Gravar no prontuário do animal
        if (consulta.getAnimal() != null) {
            Prontuario prontuario = buscarHistoricoAnimal(consulta.getAnimal().getIdAnimal());
            String registro = "[Atendimento Consulta #" + idConsulta + " - Dr(a). " +
                    (consulta.getVeterinario() != null ? consulta.getVeterinario().getNome() : "Médico") + "] Diagnóstico/Evolução: " + dadosInspecao;
            prontuarioRepository.adicionarRegistroClinico(prontuario.getIdProntuario(), registro);
        }

        // 2. Finalizar a consulta (transiciona para Realizada via Padrão State)
        consulta.finalizar(dadosInspecao);
        consultaRepository.atualizar(consulta);
    }

    public List<Consulta> listarConsultasParaAtendimento() {
        // Retorna consultas que estão Agendadas ou Em Andamento
        List<Consulta> agendadas = consultaRepository.listarPorStatus(StatusConsulta.AGENDADA);
        List<Consulta> emAndamento = consultaRepository.listarPorStatus(StatusConsulta.EM_ANDAMENTO);
        agendadas.addAll(emAndamento);
        return agendadas;
    }

    public List<Exame> listarExamesDoProntuario(int idProntuario) {
        return exameRepository.listarPorProntuario(idProntuario);
    }

    public List<Vacina> listarVacinasDoProntuario(int idProntuario) {
        return vacinaRepository.listarPorProntuario(idProntuario);
    }
}
