
package estruturas;

import model.Disciplina;

import java.util.ArrayList;
import java.util.List;

public class ArvoreAVL {

    private static class No {
        Disciplina disciplina;
        No esquerda;
        No direita;
        int altura;

        No(Disciplina disciplina) {
            this.disciplina = disciplina;
            this.altura = 1;
        }
    }

    private No raiz;

    private int altura(No no) {
        return no == null ? 0 : no.altura;
    }

    private int fatorBalanceamento(No no) {
        return no == null ? 0 :
                altura(no.esquerda) - altura(no.direita);
    }

    private No rotacaoDireita(No y) {
        No x = y.esquerda;
        No temp = x.direita;

        x.direita = y;
        y.esquerda = temp;

        y.altura = 1 + Math.max(altura(y.esquerda), altura(y.direita));
        x.altura = 1 + Math.max(altura(x.esquerda), altura(x.direita));

        return x;
    }

    private No rotacaoEsquerda(No x) {
        No y = x.direita;
        No temp = y.esquerda;

        y.esquerda = x;
        x.direita = temp;

        x.altura = 1 + Math.max(altura(x.esquerda), altura(x.direita));
        y.altura = 1 + Math.max(altura(y.esquerda), altura(y.direita));

        return y;
    }


    public boolean inserir(Disciplina disciplina) {
        if (disciplina == null || disciplina.getCodigo() == null) {
            return false;
        }

        if (buscar(disciplina.getCodigo()) != null) {
            return false;
        }

        raiz = inserirRecursivo(raiz, disciplina);
        return true;
    }

    private No inserirRecursivo(No no, Disciplina disciplina) {
        if (no == null) {
            return new No(disciplina);
        }

        int comparacao = disciplina.getCodigo()
                .compareTo(no.disciplina.getCodigo());

        if (comparacao < 0) {
            no.esquerda = inserirRecursivo(no.esquerda, disciplina);
        } else if (comparacao > 0) {
            no.direita = inserirRecursivo(no.direita, disciplina);
        } else {
            return no;
        }

        no.altura = 1 + Math.max(altura(no.esquerda), altura(no.direita));

        int fb = fatorBalanceamento(no);

        if (fb > 1 && disciplina.getCodigo()
                .compareTo(no.esquerda.disciplina.getCodigo()) < 0) {
            return rotacaoDireita(no);
        }

        if (fb < -1 && disciplina.getCodigo()
                .compareTo(no.direita.disciplina.getCodigo()) > 0) {
            return rotacaoEsquerda(no);
        }

        if (fb > 1 && disciplina.getCodigo()
                .compareTo(no.esquerda.disciplina.getCodigo()) > 0) {
            no.esquerda = rotacaoEsquerda(no.esquerda);
            return rotacaoDireita(no);
        }

        if (fb < -1 && disciplina.getCodigo()
                .compareTo(no.direita.disciplina.getCodigo()) < 0) {
            no.direita = rotacaoDireita(no.direita);
            return rotacaoEsquerda(no);
        }

        return no;
    }

    public Disciplina buscar(String codigo) {
        if (codigo == null) {
            return null;
        }

        No atual = raiz;

        while (atual != null) {
            int comparacao = codigo.compareTo(
                    atual.disciplina.getCodigo()
            );

            if (comparacao == 0) {
                return atual.disciplina;
            }

            if (comparacao < 0) {
                atual = atual.esquerda;
            } else {
                atual = atual.direita;
            }
        }

        return null;
    }


    public boolean remover(String codigo) {
        if (codigo == null || buscar(codigo) == null) {
            return false;
        }

        raiz = removerRecursivo(raiz, codigo);
        return true;
    }

    private No menorNo(No no) {
        while (no.esquerda != null) {
            no = no.esquerda;
        }
        return no;
    }

    private No removerRecursivo(No no, String codigo) {
        if (no == null) {
            return null;
        }

        int comparacao = codigo.compareTo(no.disciplina.getCodigo());

        if (comparacao < 0) {
            no.esquerda = removerRecursivo(no.esquerda, codigo);
        } else if (comparacao > 0) {
            no.direita = removerRecursivo(no.direita, codigo);
        } else {
            if (no.esquerda == null || no.direita == null) {
                no = (no.esquerda != null) ? no.esquerda : no.direita;
            } else {
                No sucessor = menorNo(no.direita);
                no.disciplina = sucessor.disciplina;
                no.direita = removerRecursivo(
                        no.direita, sucessor.disciplina.getCodigo()
                );
            }
        }

        if (no == null) {
            return null;
        }

        no.altura = 1 + Math.max(altura(no.esquerda), altura(no.direita));

        int fb = fatorBalanceamento(no);

        if (fb > 1 && fatorBalanceamento(no.esquerda) >= 0) {
            return rotacaoDireita(no);
        }

        if (fb > 1 && fatorBalanceamento(no.esquerda) < 0) {
            no.esquerda = rotacaoEsquerda(no.esquerda);
            return rotacaoDireita(no);
        }

        if (fb < -1 && fatorBalanceamento(no.direita) <= 0) {
            return rotacaoEsquerda(no);
        }

        if (fb < -1 && fatorBalanceamento(no.direita) > 0) {
            no.direita = rotacaoDireita(no.direita);
            return rotacaoEsquerda(no);
        }

        return no;
    }


    public String getCodigoRaiz() {
        return raiz == null ? null : raiz.disciplina.getCodigo();
    }

    public List<Disciplina> listar() {
        List<Disciplina> disciplinas = new ArrayList<>();
        listarEmOrdem(raiz, disciplinas);
        return disciplinas;
    }

    private void listarEmOrdem(No no, List<Disciplina> disciplinas) {
        if (no == null) {
            return;
        }

        listarEmOrdem(no.esquerda, disciplinas);
        disciplinas.add(no.disciplina);
        listarEmOrdem(no.direita, disciplinas);
    }


}
