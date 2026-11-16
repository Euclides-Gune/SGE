
package interfaces;

import estruturas.*;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.regex.Pattern;

public class PainelDisciplinas extends JPanel {

    private final ArvoreAVL dados;
    private final GestaoInscricoes gestao;
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private final TableRowSorter<DefaultTableModel> filtro;
    private final JTextField pesquisa = Estilo.field();
    private final JLabel conta = Estilo.label("", 13, false, Estilo.MUTED);

    public PainelDisciplinas(ArvoreAVL d, GestaoInscricoes g) {
        dados = d;
        gestao = g;

        setLayout(new BorderLayout());

        add(Estilo.page(
            "Gestão de Disciplinas",
            "Organize o catálogo académico das disciplinas.",
            "book"
        ));

        JPanel page = (JPanel) getComponent(0);

        Estilo.Rounded c = Estilo.card(new BorderLayout(0, 18));
        page.add(c);

        JPanel toolbar = Estilo.clear(new BorderLayout(10, 0));
        JPanel buttons = Estilo.actions();

        JButton add = Estilo.btn("Adicionar", "plus", Estilo.GREEN);
        JButton edit = Estilo.btn("Editar", "edit", Estilo.BLUE);
        JButton remove = Estilo.btn("Remover", "delete", Estilo.RED);
        JButton reload = Estilo.outline("Recarregar", "refresh");

        buttons.add(add);
        buttons.add(edit);
        buttons.add(remove);
        buttons.add(reload);

        toolbar.add(buttons, BorderLayout.WEST);

        pesquisa.setColumns(20);
        pesquisa.setToolTipText("Pesquisar código ou nome");

        toolbar.add(pesquisa, BorderLayout.EAST);
        c.add(toolbar, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
            new String[]{"Código", "Nome da disciplina", "Créditos", "Semestre"},
            0
        ) {
            public boolean isCellEditable(int r, int col) {
                return false;
            }
        };

        tabela = new JTable(modelo);

        Estilo.table(tabela);

        tabela.getColumnModel().getColumn(1).setPreferredWidth(350);

        filtro = new TableRowSorter<>(modelo);
        tabela.setRowSorter(filtro);

        c.add(Estilo.scroll(tabela));
        c.add(conta, BorderLayout.SOUTH);

        pesquisa.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        add.addActionListener(e -> novo());
        edit.addActionListener(e -> editar());
        remove.addActionListener(e -> remover());

        reload.addActionListener(e -> {
            pesquisa.setText("");
            actualizarTabela();
        });

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2)
                    editar();
            }
        });

        actualizarTabela();
    }

    private void filtrar() {
        String s = pesquisa.getText().trim();

        filtro.setRowFilter(
            s.isEmpty()
                ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(s), 0, 1)
        );

        conta.setText(tabela.getRowCount() + " disciplina(s) visível(is)");
    }

    public void actualizarTabela() {
        modelo.setRowCount(0);

        for (Disciplina d : dados.listar())
            modelo.addRow(new Object[]{
                d.getCodigo(),
                d.getNome(),
                d.getCreditos(),
                d.getSemestre()
            });

        conta.setText(tabela.getRowCount() + " disciplina(s) visível(is)");
    }

    private String selected() {
        int r = tabela.getSelectedRow();

        if (r < 0) {
            Estilo.warn(this, "Seleccione uma disciplina na tabela.");
            return null;
        }

        return modelo.getValueAt(
            tabela.convertRowIndexToModel(r), 0
        ).toString();
    }

    private void novo() {
        Formulario f = new Formulario(null);

        if (!f.show("Adicionar disciplina"))
            return;

        try {
            Disciplina n = f.build();

            if (!dados.inserir(n)) {
                Estilo.warn(this, "Já existe uma disciplina com esse código.");
                return;
            }

            actualizarTabela();

            Estilo.msg(this, "Disciplina adicionada com sucesso!");

        } catch (IllegalArgumentException ex) {
            Estilo.warn(this, ex.getMessage());
        }
    }

    private void editar() {
        String code = selected();

        if (code == null)
            return;

        Disciplina old = dados.buscar(code);

        if (old == null) {
            Estilo.warn(this, "Disciplina não encontrada.");
            return;
        }

        Formulario f = new Formulario(old);

        if (!f.show("Editar disciplina"))
            return;

        try {
            Disciplina n = f.build();

            if (!dados.remover(code)) {
                Estilo.warn(this, "Não foi possível editar.");
                return;
            }

            if (!dados.inserir(n)) {
                dados.inserir(old);
                Estilo.warn(this, "Não foi possível guardar.");
                return;
            }

            actualizarTabela();

            Estilo.msg(this, "Disciplina actualizada com sucesso!");

        } catch (IllegalArgumentException ex) {
            Estilo.warn(this, ex.getMessage());
        }
    }

    private void remover() {
        String code = selected();

        if (code == null || !Estilo.yes(this, "Remover a disciplina " + code + "?"))
            return;

        if (!gestao.removerDisciplina(code)) {
            Estilo.warn(
                this,
                "Não foi possível remover. Pode haver inscrições activas."
            );

            return;
        }

        actualizarTabela();

        Estilo.msg(this, "Disciplina removida com sucesso.");
    }

    private class Formulario {

        final JTextField code = Estilo.field();
        final JTextField name = Estilo.field();
        final JTextField credits = Estilo.field();
        final JTextField semester = Estilo.field();

        final JPanel p = Estilo.form();

        Formulario(Disciplina d) {
            code.setColumns(22);

            Estilo.row(p, "Código", code, 0);
            Estilo.row(p, "Nome", name, 1);
            Estilo.row(p, "Créditos", credits, 2);
            Estilo.row(p, "Semestre", semester, 3);

            if (d != null) {
                code.setText(d.getCodigo());
                code.setEditable(false);
                code.setBackground(new Color(240, 245, 250));

                name.setText(d.getNome());
                credits.setText("" + d.getCreditos());
                semester.setText("" + d.getSemestre());
            }
        }

        boolean show(String title) {
            return JOptionPane.showConfirmDialog(
                PainelDisciplinas.this,
                p,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            ) == JOptionPane.OK_OPTION;
        }

        Disciplina build() {
            String c = code.getText().trim();
            String n = name.getText().trim();

            if (
                c.isEmpty() ||
                n.isEmpty() ||
                credits.getText().trim().isEmpty() ||
                semester.getText().trim().isEmpty()
            )
                throw new IllegalArgumentException("Preencha todos os campos.");

            int cr, sm;

            try {
                cr = Integer.parseInt(credits.getText().trim());
                sm = Integer.parseInt(semester.getText().trim());

            } catch (NumberFormatException x) {
                throw new IllegalArgumentException(
                    "Créditos e semestre devem ser números inteiros."
                );
            }

            if (cr <= 0 || sm <= 0)
                throw new IllegalArgumentException(
                    "Créditos e semestre devem ser positivos."
                );

            return new Disciplina(c, n, cr, sm);
        }
    }
}
