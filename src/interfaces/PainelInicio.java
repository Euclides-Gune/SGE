package interfaces;

import estruturas.*;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PainelInicio extends JPanel {

    final TabelaHash e;
    final ArvoreAVL d;
    final GestaoInscricoes g;

    final JLabel[] valores = new JLabel[4];
    final JLabel[] resumo = new JLabel[4];

    public PainelInicio(TabelaHash e, ArvoreAVL d, GestaoInscricoes g) {
        this.e = e;
        this.d = d;
        this.g = g;

        setBackground(Estilo.BG);
        setLayout(new BorderLayout());

        JPanel main = Estilo.clear(new BorderLayout(0, 20));
        main.setBorder(new EmptyBorder(27, 28, 27, 28));

        add(main);

        Estilo.Rounded hero = new Estilo.Rounded(
            new Color(229, 243, 255),
            new Color(200, 224, 251),
            18
        );

        hero.setLayout(new BorderLayout(15, 0));
        hero.setBorder(new EmptyBorder(22, 24, 22, 20));

        JPanel headings = Estilo.clear(new GridLayout(2, 1));

        headings.add(Estilo.label(
            "Bem-vindo ao Sistema de Gestão Académica",
            25,
            true,
            Estilo.TEXT
        ));

        headings.add(Estilo.label(
            "Organize estudantes, disciplinas e inscrições num só lugar.",
            13,
            false,
            Estilo.MUTED
        ));

        hero.add(headings, BorderLayout.CENTER);
        hero.add(new Campus(), BorderLayout.EAST);

        main.add(hero, BorderLayout.NORTH);

        JPanel body = Estilo.clear(new BorderLayout(0, 19));
        JPanel metrics = Estilo.clear(new GridLayout(1, 4, 13, 0));

        String[] texts = {
            "Estudantes",
            "Disciplinas",
            "Inscrições activas",
            "Canceladas"
        };

        String[] icons = {
            "users",
            "book",
            "clip",
            "x"
        };

        Color[] cols = {
            new Color(231, 243, 255),
            new Color(232, 250, 240),
            new Color(244, 235, 255),
            new Color(255, 239, 239)
        };

        Color[] ink = {
            Estilo.BLUE,
            Estilo.GREEN,
            new Color(124, 72, 199),
            Estilo.RED
        };

        for (int i = 0; i < 4; i++) {
            Estilo.Rounded c = new Estilo.Rounded(
                cols[i],
                new Color(210, 223, 237),
                16
            );

            c.setLayout(new BorderLayout(6, 0));
            c.setBorder(new EmptyBorder(18, 14, 18, 14));

            JPanel words = Estilo.clear(new GridLayout(2, 1));

            words.add(Estilo.label(texts[i], 13, true, Estilo.TEXT));

            valores[i] = Estilo.label("0", 31, true, Estilo.TEXT);

            words.add(valores[i]);

            c.add(words);

            c.add(
                new JLabel(new Estilo.Glyph(icons[i], 35, ink[i])),
                BorderLayout.EAST
            );

            metrics.add(c);
        }

        body.add(metrics, BorderLayout.NORTH);

        JPanel sections = Estilo.clear(new GridLayout(1, 2, 16, 0));

        Estilo.Rounded shortcuts = Estilo.card(new BorderLayout(0, 16));

        shortcuts.add(
            Estilo.label("Acesso rápido", 20, true, Estilo.TEXT),
            BorderLayout.NORTH
        );

        JPanel links = Estilo.clear(new GridLayout(2, 2, 11, 11));

        links.add(link("Gerir Estudantes", "users", "Estudantes"));
        links.add(link("Gerir Disciplinas", "book", "Disciplinas"));
        links.add(link("Gerir Inscrições", "clip", "Inscrições"));
        links.add(link("Consultar Dados", "search", "Consultas"));

        shortcuts.add(links);
        sections.add(shortcuts);

        Estilo.Rounded summary = Estilo.card(new BorderLayout(0, 16));

        summary.add(
            Estilo.label("Resumo do sistema", 20, true, Estilo.TEXT),
            BorderLayout.NORTH
        );

        JPanel rows = Estilo.clear(new GridLayout(4, 1, 0, 8));

        String[] desc = {
            "Estudantes registados",
            "Disciplinas registadas",
            "Inscrições activas",
            "Inscrições canceladas"
        };

        for (int i = 0; i < 4; i++) {
            JPanel r = new JPanel(new BorderLayout());

            r.setBackground(
                i % 2 == 0
                    ? new Color(246, 250, 255)
                    : Color.WHITE
            );

            r.setBorder(new EmptyBorder(13, 12, 13, 12));

            r.add(
                Estilo.label(desc[i], 14, false, Estilo.TEXT),
                BorderLayout.WEST
            );

            resumo[i] = Estilo.label("0", 17, true, Estilo.BLUE);

            r.add(resumo[i], BorderLayout.EAST);

            rows.add(r);
        }

        summary.add(rows);
        sections.add(summary);

        body.add(sections);
        main.add(body);

        actualizar();
    }

    JButton link(String text, String icon, String page) {
        JButton b = new JButton(
            text,
            new Estilo.Glyph(icon, 26, Estilo.BLUE)
        );

        b.setFont(Estilo.f(13, true));
        b.setForeground(Estilo.TEXT);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setIconTextGap(12);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBackground(new Color(249, 251, 255));

        b.setBorder(
            new javax.swing.border.CompoundBorder(
                new javax.swing.border.LineBorder(Estilo.BORDER),
                new EmptyBorder(15, 10, 15, 10)
            )
        );

        b.addActionListener(ev -> {
            Window w = SwingUtilities.getWindowAncestor(this);

            if (w instanceof JanelaPrincipal)
                ((JanelaPrincipal) w).irPara(page);
        });

        return b;
    }

    public void actualizar() {
        String[] n = {
            String.valueOf(e.listar().size()),

            String.valueOf(d.listar().size()),

            String.valueOf(
                g.listar().stream()
                    .filter(i -> "Ativa".equalsIgnoreCase(i.getEstado()))
                    .count()
            ),

            String.valueOf(
                g.listar().stream()
                    .filter(i -> "Cancelada".equalsIgnoreCase(i.getEstado()))
                    .count()
            )
        };

        for (int i = 0; i < 4; i++) {
            valores[i].setText(n[i]);
            resumo[i].setText(n[i]);
        }
    }

    static class Campus extends JComponent {

        BufferedImage img;

        Campus() {
            setPreferredSize(new Dimension(195, 85));

            try {
                URL u = getClass().getResource(
                    "/assets/campus_ilustracao.png"
                );

                if (u != null)
                    img = ImageIO.read(u);

                else {
                    File f = new File("src/assets/campus_ilustracao.png");

                    if (f.exists())
                        img = ImageIO.read(f);
                }

            } catch (Exception ignored) {
            }
        }

        protected void paintComponent(Graphics g) {
            if (img != null)
                g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
