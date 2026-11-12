
package estruturas;

import model.Estudante;

import java.util.ArrayList;
import java.util.List;

public class TabelaHash {

    private static class No {
        Estudante estudante;
        No proximo;

        No(Estudante estudante) {
            this.estudante = estudante;
            this.proximo = null;
        }
    }

    private No[] tabela;
    private int capacidade;

    public TabelaHash(int capacidade) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade inválida");
        }

        this.capacidade = capacidade;
        this.tabela = new No[capacidade];
    }

    private int funcaoHash(String codigo) {
        return Math.floorMod(codigo.hashCode(), capacidade);
    }


    public boolean inserir(Estudante estudante) {
        if (estudante == null || estudante.getCodigo() == null) {
            return false;
        }

        int indice = funcaoHash(estudante.getCodigo());
        No atual = tabela[indice];

        while (atual != null) {
            if (atual.estudante.getCodigo().equals(estudante.getCodigo())) {
                return false;
            }
            atual = atual.proximo;
        }

        No novo = new No(estudante);
        novo.proximo = tabela[indice];
        tabela[indice] = novo;

        return true;
    }


    public Estudante buscar(String codigo) {
        if (codigo == null) {
            return null;
        }

        int indice = funcaoHash(codigo);
        No atual = tabela[indice];

        while (atual != null) {
            if (atual.estudante.getCodigo().equals(codigo)) {
                return atual.estudante;
            }

            atual = atual.proximo;
        }

        return null;
    }

    public boolean remover(String codigo) {
        if (codigo == null) {
            return false;
        }

        int indice = funcaoHash(codigo);
        No atual = tabela[indice];
        No anterior = null;

        while (atual != null) {
            if (atual.estudante.getCodigo().equals(codigo)) {
                if (anterior == null) {
                    tabela[indice] = atual.proximo;
                } else {
                    anterior.proximo = atual.proximo;
                }

                return true;
            }

            anterior = atual;
            atual = atual.proximo;
        }

        return false;
    }

    public List<Estudante> listar() {
        List<Estudante> estudantes = new ArrayList<>();

        for (int i = 0; i < capacidade; i++) {
            No atual = tabela[i];

            while (atual != null) {
                estudantes.add(atual.estudante);
                atual = atual.proximo;
            }
        }

        return estudantes;
    }

}
