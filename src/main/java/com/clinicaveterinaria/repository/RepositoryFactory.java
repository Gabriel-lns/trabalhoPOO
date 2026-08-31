package com.clinicaveterinaria.repository;

import com.clinicaveterinaria.repository.sqlite.SqliteRepositoryFactory;

/**
 * Fábrica Abstrata de Repositórios (DIP & Abstract Factory).
 * Permite que os Controladores obtenham instâncias de repositório sem se acoplar ao SQLite.
 */
public interface RepositoryFactory {

    TutorRepository getTutorRepository();
    VeterinarioRepository getVeterinarioRepository();
    AnimalRepository getAnimalRepository();
    ProntuarioRepository getProntuarioRepository();
    ExameRepository getExameRepository();
    VacinaRepository getVacinaRepository();
    ConsultaRepository getConsultaRepository();
    PagamentoRepository getPagamentoRepository();

    /**
     * Retorna a implementação padrão ativa (SQLite).
     */
    static RepositoryFactory getInstance() {
        return SqliteRepositoryFactory.getInstance();
    }
}
