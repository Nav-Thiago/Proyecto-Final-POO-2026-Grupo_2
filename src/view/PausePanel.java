package view;

import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PausePanel extends JPanel {

    public PausePanel(Runnable onResume, Runnable onQuit) {
        setOpaque(false);
        setFocusable(true);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("PAUSA", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        add(title, gbc);

        MenuButton resumeButton = new MenuButton("Reanudar");
        MenuButton quitButton = new MenuButton("Salir al menú");

        Dimension buttonSize = new Dimension(280, 50);
        resumeButton.setPreferredSize(buttonSize);
        quitButton.setPreferredSize(buttonSize);

        resumeButton.addActionListener(e -> onResume.run());
        quitButton.addActionListener(e -> onQuit.run());

        KeyAdapter escapeListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    onResume.run();
                }
            }
        };
        addKeyListener(escapeListener);
        resumeButton.addKeyListener(escapeListener);
        quitButton.addKeyListener(escapeListener);

        gbc.gridy = 1;
        add(resumeButton, gbc);

        gbc.gridy = 2;
        add(quitButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } finally {
            g2.dispose();
        }
    }
}