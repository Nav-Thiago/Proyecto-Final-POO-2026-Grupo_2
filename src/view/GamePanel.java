package view;

import model.Player;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private final Player player;

    public  GamePanel(Player player) {
        this.player = player;
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(30,30, 35));
        setFocusable(true);
    }

    public void update() {
        player.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE);
        g2.fillOval((int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight());
    }
}
