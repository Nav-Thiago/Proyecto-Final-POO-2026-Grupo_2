package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SplashSubject extends SplashPanel {

    public SplashSubject() {
        super(Color.BLACK);
        initializeComponents();
    }

    @Override 
    protected void initializeComponents() {
        JLabel lblSubject = createTag(
            "PROGRAMACIÓN ORIENTADA A OBJETOS",
            new Font("Serif", Font.BOLD, 48),
            new Color(212, 175, 55)
        );

        JLabel lblJob = createTag(
            "Proyecto Integrador Final",
            new Font("SansSerif", Font.PLAIN, 28),
            Color.LIGHT_GRAY
        );

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(lblSubject, BorderLayout.CENTER);
        centerPanel.add(lblJob, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }
}
