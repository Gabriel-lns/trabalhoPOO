package com.clinicaveterinaria.repository.sqlite;

import com.clinicaveterinaria.repository.*;

/**
 * Implementação Singleton da Fábrica de Repositórios para SQLite.
 */
public class SqliteRepositoryFactory implements RepositoryFactory {
    private static SqliteRepositoryFactory instance;

    private final TutorRepository tutorRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final AnimalRepository animalRepository;
    private final ProntuarioRepository prontuarioRepository;
    private final ExameRepository exameRepository;
    private final VacinaRepository vacinaRepository;
    private final ConsultaRepository consultaRepository;
    private final PagamentoRepository pagamentoRepository;

    private SqliteRepositoryFactory() {
        this.tutorRepository = new SqliteTutorRepository();
        this.veterinarioRepository = new SqliteVeterinarioRepository();
        this.prontuarioRepository = new SqliteProntuarioRepository();
        this.animalRepository = new SqliteAnimalRepository(this.tutorRepository, this.prontuarioRepository);
        this.exameRepository = new SqliteExameRepository();
        this.vacinaRepository = new SqliteVacinaRepository();
        this.consultaRepository = new SqliteConsultaRepository(this.animalRepository, this.veterinarioRepository);
        this.pagamentoRepository = new SqlitePagamentoRepository();
    }

    public static synchronized SqliteRepositoryFactory getInstance() {
        if (instance == null) {
            instance = new SqliteRepositoryFactory();
        }
        return instance;
    }

    @Override
    public TutorRepository getTutorRepository() {
        return tutorRepository;
    }

    @Override
    public VeterinarioRepository getVeterinarioRepository() {
        return veterinarioRepository;
    }

    @Override
    public AnimalRepository getAnimalRepository() {
        return animalRepository;
    }

    @Override
    public ProntuarioRepository getProntuarioRepository() {
        return prontuarioRepository;
    }

    @Override
    public ExameRepository getExameRepository() {
        return exameRepository;
    }

    @Override
    public VacinaRepository getVacinaRepository() {
        return vacinaRepository;
    }

    @Override
    public ConsultaRepository getConsultaRepository() {
        return consultaRepository;
    }

    @Override
    public PagamentoRepository getPagamentoRepository() {
        return pagamentoRepository;
    }
}
