package view;

import model.Enemy;
import model.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GamePanel extends JPanel {

    private final Player player;
    private final Enemy zombie;
    private BufferedImage playerSprite;

    public  GamePanel(Player player, Enemy zombie) {
        this.player = player;
        this.zombie = zombie;
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(30,30, 35));
        setFocusable(true);
        loadSprites();
    }

    private void loadSprites() {
        try (InputStream in = getClass().getResourceAsStream("/Resources/imagenes/idle_down.png")) {
            if (in == null) {
                throw new IOException("No se encontró /imagenes/idle_down.png en el classpath");
            }
            playerSprite = ImageIO.read(in);
        } catch (IOException e) {
            System.err.println("Error cargando sprite del jugador: " + e.getMessage() );
        }
    }

    public void update() {
        if (player.isAlive()) {
            player.update(getWidth(), getHeight());
            zombie.update(player);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.RED);
        g2.fillOval((int) zombie.getX(), (int) zombie.getY(), zombie.getWidth(), zombie.getHeight());

        if (playerSprite != null) {
            g2.drawImage(playerSprite, (int) player.getX(), (int) player.getY(), (int) player.getWidth(), (int) player.getHeight(), null);
        } else {
            g2.setColor(Color.BLUE);
            g2.fillOval((int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight());
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20)); // Configura la tipografía

        String textoVida = "Supervivencia: " + player.getHealth() + " / " + player.getMaxHealth();

        g2.drawString(textoVida, 20, 30); // Dibuja el texto en la esquina superior izquierda
    }
}
