
package model;

public class Disciplina {
    private String codigo;
    private String nome;
    private int creditos;
    private int semestre;

    public Disciplina(String codigo, String nome, int creditos, int semestre) {
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
        this.semestre = semestre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public int getSemestre() {
        return semestre;
    }
}
