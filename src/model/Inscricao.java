
package model;

import java.time.LocalDate;

public class Inscricao {
    private String codigoEstudante;
    private String codigoDisciplina;
    private LocalDate dataInscricao;
    private String estado;

    public Inscricao(String codigoEstudante, String codigoDisciplina,
                     LocalDate dataInscricao, String estado) {
        this.codigoEstudante = codigoEstudante;
        this.codigoDisciplina = codigoDisciplina;
        this.dataInscricao = dataInscricao;
        this.estado = estado;
    }

    public String getCodigoEstudante() {
        return codigoEstudante;
    }

    public String getCodigoDisciplina() {
        return codigoDisciplina;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
