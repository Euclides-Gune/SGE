package model;

import java.time.LocalDate;

public class Estudante {
    private String codigo;
    private String nome;
    private String curso;
    private int anoIngresso;
    private LocalDate dataNascimento;

    public Estudante(String codigo, String nome, String curso,
                     int anoIngresso, LocalDate dataNascimento) {
        this.codigo = codigo;
        this.nome = nome;
        this.curso = curso;
        this.anoIngresso = anoIngresso;
        this.dataNascimento = dataNascimento;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getCurso() {
        return curso;
    }

    public int getAnoIngresso() {
        return anoIngresso;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
}
