package view;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {

    private final JLabel titleLabel = new JLabel();
    private final JLabel timeLabel = new JLabel();
    private final JLabel bestLabel = new JLabel();
    private final JLabel recordLabel = new JLabel("NUEVO RÉCORD");
    private final MenuButton backButton = new MenuButton("Volver al menú");

    public GameOverPanel() {
        setBackground(new Color(15, 15, 15));
        setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        styleLabel(titleLabel, 56, Color.WHITE);
        styleLabel(timeLabel, 24, Color.WHITE);
        styleLabel(bestLabel, 24, Color.WHITE);
        styleLabel(recordLabel, 24, Color.YELLOW);
        backButton.setAlignmentX(CENTER_ALIGNMENT);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(30));
        content.add(timeLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(bestLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(recordLabel);
        content.add(Box.createVerticalStrut(40));
        content.add(backButton);

        add(content);
    }

    private void styleLabel(JLabel label, int size, Color color) {
        label.setFont(new Font("Monospaced", Font.BOLD, size));
        label.setForeground(color);
        label.setAlignmentX(CENTER_ALIGNMENT);
    }

    public void showResult(boolean victory, long timeMs, long bestTimeMs, boolean newRecord) {
        titleLabel.setText(victory ? "GANASTE" : "JUEGO TERMINADO");
        titleLabel.setForeground(victory ? Color.GREEN : Color.RED);
        timeLabel.setText("Tu tiempo: " + formatTime(timeMs));

        bestLabel.setVisible(victory);
        recordLabel.setVisible(victory && newRecord);
        if (victory) {
            bestLabel.setText("Mejor tiempo: " + formatTime(bestTimeMs));
        }
    }

    private String formatTime(long ms) {
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public MenuButton getBackButton() {
        return backButton;
    }
}
