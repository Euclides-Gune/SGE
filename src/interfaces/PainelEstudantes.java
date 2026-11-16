package interfaces;

import estruturas.*;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.time.*;
import java.util.regex.Pattern;

public class PainelEstudantes extends JPanel {

    private final TabelaHash dados;
    private final GestaoInscricoes gestao;
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private final TableRowSorter<DefaultTableModel> filtro;
    private final JTextField pesquisa = Estilo.field();
    private final JLabel conta = Estilo.label("", 13, false, Estilo.MUTED);

    public PainelEstudantes(TabelaHash d, GestaoInscricoes g) {
        dados = d;
        gestao = g;

        setLayout(new BorderLayout());

        add(Estilo.page(
            "Gestão de Estudantes",
            "Cadastre, edite, pesquise e remova estudantes.",
            "users"
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

        pesquisa.setColumns(19);
        pesquisa.setToolTipText("Pesquisar código, nome ou curso");

        toolbar.add(pesquisa, BorderLayout.EAST);
        c.add(toolbar, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
            new String[]{
                "Código",
                "Nome completo",
                "Curso",
                "Ano de ingresso",
                "Nascimento"
            },
            0
        ) {
            public boolean isCellEditable(int r, int col) {
                return false;
            }
        };

        tabela = new JTable(modelo);

        Estilo.table(tabela);

        tabela.getColumnModel().getColumn(1).setPreferredWidth(225);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(210);

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
                : RowFilter.regexFilter("(?i)" + Pattern.quote(s), 0, 1, 2)
        );

        conta.setText(tabela.getRowCount() + " estudante(s) visível(is)");
    }

    public void actualizarTabela() {
        modelo.setRowCount(0);

        for (Estudante e : dados.listar())
            modelo.addRow(new Object[]{
                e.getCodigo(),
                e.getNome(),
                e.getCurso(),
                e.getAnoIngresso(),
                e.getDataNascimento()
            });

        conta.setText(tabela.getRowCount() + " estudante(s) visível(is)");
    }

    private String selected() {
        int r = tabela.getSelectedRow();

        if (r < 0) {
            Estilo.warn(this, "Seleccione um estudante na tabela.");
            return null;
        }

        return modelo.getValueAt(
            tabela.convertRowIndexToModel(r), 0
        ).toString();
    }

    private void novo() {
        Formulario f = new Formulario(null);

        if (!f.show("Adicionar estudante"))
            return;

        try {
            Estudante n = f.build();

            if (!dados.inserir(n)) {
                Estilo.warn(this, "Já existe um estudante com esse código.");
                return;
            }

            actualizarTabela();

            Estilo.msg(this, "Estudante adicionado com sucesso!");

        } catch (IllegalArgumentException ex) {
            Estilo.warn(this, ex.getMessage());
        }
    }

    private void editar() {
        String code = selected();

        if (code == null)
            return;

        Estudante old = dados.buscar(code);

        if (old == null) {
            Estilo.warn(this, "Estudante não encontrado.");
            return;
        }

        Formulario f = new Formulario(old);

        if (!f.show("Editar estudante"))
            return;

        try {
            Estudante n = f.build();

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

            Estilo.msg(this, "Estudante actualizado com sucesso!");

        } catch (IllegalArgumentException ex) {
            Estilo.warn(this, ex.getMessage());
        }
    }

    private void remover() {
        String code = selected();

        if (code == null || !Estilo.yes(this, "Remover o estudante " + code + "?"))
            return;

        if (!gestao.removerEstudante(code)) {
            Estilo.warn(
                this,
                "Não foi possível remover. Pode haver inscrições activas."
            );

            return;
        }

        actualizarTabela();

        Estilo.msg(this, "Estudante removido com sucesso.");
    }

    private class Formulario {

        final JTextField code = Estilo.field();
        final JTextField name = Estilo.field();
        final JTextField course = Estilo.field();
        final JTextField year = Estilo.field();
        final JTextField birth = Estilo.field();

        final JPanel p = Estilo.form();

        Formulario(Estudante e) {
            code.setColumns(22);

            Estilo.row(p, "Código", code, 0);
            Estilo.row(p, "Nome completo", name, 1);
            Estilo.row(p, "Curso", course, 2);
            Estilo.row(p, "Ano de ingresso", year, 3);
            Estilo.row(p, "Nascimento (AAAA-MM-DD)", birth, 4);

            if (e != null) {
                code.setText(e.getCodigo());
                code.setEditable(false);
                code.setBackground(new Color(240, 245, 250));

                name.setText(e.getNome());
                course.setText(e.getCurso());
                year.setText("" + e.getAnoIngresso());
                birth.setText(e.getDataNascimento().toString());
            }
        }

        boolean show(String title) {
            return JOptionPane.showConfirmDialog(
                PainelEstudantes.this,
                p,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            ) == JOptionPane.OK_OPTION;
        }

        Estudante build() {
            String c = code.getText().trim();
            String n = name.getText().trim();
            String cu = course.getText().trim();

            if (
                c.isEmpty() ||
                n.isEmpty() ||
                cu.isEmpty() ||
                year.getText().trim().isEmpty() ||
                birth.getText().trim().isEmpty()
            )
                throw new IllegalArgumentException("Preencha todos os campos.");

            int y;

            try {
                y = Integer.parseInt(year.getText().trim());

            } catch (NumberFormatException x) {
                throw new IllegalArgumentException("Ano de ingresso inválido.");
            }

            if (y < 1900 || y > LocalDate.now().getYear() + 1)
                throw new IllegalArgumentException(
                    "Ano de ingresso fora do intervalo válido."
                );

            LocalDate b;

            try {
                b = LocalDate.parse(birth.getText().trim());

            } catch (Exception x) {
                throw new IllegalArgumentException(
                    "Data inválida. Use AAAA-MM-DD."
                );
            }

            if (b.isAfter(LocalDate.now()))
                throw new IllegalArgumentException(
                    "Nascimento não pode ser futuro."
                );

            return new Estudante(c, n, cu, y, b);
        }
    }
}
