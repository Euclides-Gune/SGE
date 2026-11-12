
package estruturas;

import model.Inscricao;
import java.time.LocalDate;
import java.util.ArrayList;

public class GestaoInscricoes {

    private TabelaHash estudantes;
    private ArvoreAVL disciplinas;
    private ArrayList<Inscricao> inscricoes;

    public GestaoInscricoes(TabelaHash estudantes, ArvoreAVL disciplinas) {
        this.estudantes = estudantes;
        this.disciplinas = disciplinas;
        this.inscricoes = new ArrayList<>();
    }

    public boolean inscrever(String codigoEstudante, String codigoDisciplina) {

        if (estudantes.buscar(codigoEstudante) == null ||
                disciplinas.buscar(codigoDisciplina) == null) {
            return false;
        }

        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getCodigoEstudante().equals(codigoEstudante) &&
                    inscricao.getCodigoDisciplina().equals(codigoDisciplina)) {

                if (inscricao.getEstado().equals("Ativa")) {
                    return false;
                }

                inscricao.setEstado("Ativa");
                return true;
            }
        }

        Inscricao nova = new Inscricao(
                codigoEstudante,
                codigoDisciplina,
                LocalDate.now(),
                "Ativa"
        );

        inscricoes.add(nova);
        return true;
    }


    public boolean cancelar(String codigoEstudante, String codigoDisciplina) {

        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getCodigoEstudante().equals(codigoEstudante) &&
                    inscricao.getCodigoDisciplina().equals(codigoDisciplina) &&
                    inscricao.getEstado().equals("Ativa")) {

                inscricao.setEstado("Cancelada");
                return true;
            }
        }

        return false;
    }


    public ArrayList<Inscricao> consultarPorEstudante(String codigo) {
        ArrayList<Inscricao> resultado = new ArrayList<>();

        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getCodigoEstudante().equals(codigo)) {
                resultado.add(inscricao);
            }
        }

        return resultado;
    }

    public ArrayList<Inscricao> consultarPorDisciplina(String codigo) {
        ArrayList<Inscricao> resultado = new ArrayList<>();

        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getCodigoDisciplina().equals(codigo)) {
                resultado.add(inscricao);
            }
        }

        return resultado;
    }

    public ArrayList<Inscricao> listar() {
        return new ArrayList<>(inscricoes);
    }

    public boolean possuiInscricoesAtivasEstudante(String codigo) {
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getCodigoEstudante().equals(codigo)
                    && inscricao.getEstado().equals("Ativa")) {
                return true;
            }
        }
        return false;
    }

    public boolean possuiInscricoesAtivasDisciplina(String codigo) {
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getCodigoDisciplina().equals(codigo)
                    && inscricao.getEstado().equals("Ativa")) {
                return true;
            }
        }
        return false;
    }

    public boolean removerEstudante(String codigo) {
        if (possuiInscricoesAtivasEstudante(codigo)) {
            return false;
        }

        return estudantes.remover(codigo);
    }

    public boolean removerDisciplina(String codigo) {
        if (possuiInscricoesAtivasDisciplina(codigo)) {
            return false;
        }

        return disciplinas.remover(codigo);
    }

}
