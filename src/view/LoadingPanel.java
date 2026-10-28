package view;

import javax.swing.*;
import java.awt.*;

public class LoadingPanel extends JPanel {

    private final JLabel messageLabel = new JLabel("Cargando...");

    public LoadingPanel() {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout()); // centra lo que se agregue

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("CARGANDO");
        title.setFont(new Font("Monospaced", Font.BOLD, 56));
        title.setForeground(new Color(180, 0, 0));
        title.setAlignmentX(CENTER_ALIGNMENT);

        messageLabel.setFont(new Font("Monospaced", Font.PLAIN, 24));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setAlignmentX(CENTER_ALIGNMENT);

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setMaximumSize(new Dimension(400, 20));
        bar.setAlignmentX(CENTER_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(30));
        content.add(messageLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(bar);

        add(content);
    }

    public void setMessage(String text) {
        messageLabel.setText(text);
    }
}