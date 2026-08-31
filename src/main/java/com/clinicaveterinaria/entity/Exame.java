package com.clinicaveterinaria.entity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade Exame conforme modelado no Astah.
 * Representa exames clínicos solicitados ou anexados durante o atendimento.
 */
public class Exame {
    private int idExame;
    private String tipo;
    private LocalDate data;
    private String resultado;
    private int idConsultaOrigem;
    private int idProntuario;

    public Exame() {
    }

    public Exame(int idExame, String tipo, LocalDate data, String resultado, int idConsultaOrigem, int idProntuario) {
        this.idExame = idExame;
        this.tipo = Objects.requireNonNull(tipo, "Tipo de exame não pode ser nulo").trim();
        this.data = data != null ? data : LocalDate.now();
        this.resultado = resultado != null ? resultado.trim() : "Aguardando resultado";
        this.idConsultaOrigem = idConsultaOrigem;
        this.idProntuario = idProntuario;
    }

    /**
     * Anexa o laudo/resultado do exame.
     * Conforme diagrama de classes do Astah.
     */
    public void anexarResultado(String resultado) {
        if (resultado != null && !resultado.trim().isEmpty()) {
            this.resultado = resultado.trim();
        }
    }

    // Getters e Setters
    public int getIdExame() {
        return idExame;
    }

    public void setIdExame(int idExame) {
        this.idExame = idExame;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public int getIdConsultaOrigem() {
        return idConsultaOrigem;
    }

    public void setIdConsultaOrigem(int idConsultaOrigem) {
        this.idConsultaOrigem = idConsultaOrigem;
    }

    public int getIdProntuario() {
        return idProntuario;
    }

    public void setIdProntuario(int idProntuario) {
        this.idProntuario = idProntuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exame exame)) return false;
        return idExame == exame.idExame;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idExame);
    }

    @Override
    public String toString() {
        return "Exame [" + tipo + "] - Data: " + data + " - Status/Resultado: " + resultado;
    }
}
