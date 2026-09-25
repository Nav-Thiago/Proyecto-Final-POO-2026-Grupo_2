package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public abstract class SplashPanel extends JPanel {

    public SplashPanel(Color backgroundcColor) {
        this.setLayout(new BorderLayout());
        this.setBackground(backgroundcColor);
    }

    protected abstract void initializeComponents();

    protected JLabel createTag(String text, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
}
