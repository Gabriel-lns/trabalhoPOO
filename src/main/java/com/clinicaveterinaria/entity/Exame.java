package com.clinicaveterinaria.entity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade de Domínio: Exame Clínico / Laboratorial.
 */
public class Exame {
    private int idExame;
    private String tipo;
    private LocalDate data;
    private String resultado;
    private int idConsultaOrigem;
    private int idProntuario;

    public Exame() {
        this.data = LocalDate.now();
        this.resultado = "Aguardando laudo";
    }

    public Exame(int idExame, String tipo, LocalDate data, String resultado, int idConsultaOrigem, int idProntuario) {
        this.idExame = idExame;
        this.tipo = tipo;
        this.data = data != null ? data : LocalDate.now();
        this.resultado = resultado != null ? resultado : "Aguardando laudo";
        this.idConsultaOrigem = idConsultaOrigem;
        this.idProntuario = idProntuario;
    }

    // Static Factory Method
    public static Exame criarSolicitacao(int idConsulta, int idProntuario, String tipo, String resultadoInicial) {
        return new Exame(0, tipo, LocalDate.now(), resultadoInicial, idConsulta, idProntuario);
    }

    public void anexarResultado(String resultado) {
        this.resultado = resultado;
    }

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

    public LocalDate getDataSolicitacao() {
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
        if (o == null || getClass() != o.getClass()) return false;
        Exame exame = (Exame) o;
        return idExame == exame.idExame;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idExame);
    }

    @Override
    public String toString() {
        return "Exame #" + idExame + ": " + tipo + " - Data: " + data + " | Resultado: " + resultado;
    }
}
