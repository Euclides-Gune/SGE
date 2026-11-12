
import interfaces.TelaBoasVindas;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaBoasVindas().setVisible(true);
        });
    }
}
