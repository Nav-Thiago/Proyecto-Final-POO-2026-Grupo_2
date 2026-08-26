package view;

import model.Enemy;
import model.Player;
import model.Proyectil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GamePanel extends JPanel {

    private final Player player;
    private final Enemy zombie;
    private final List<Proyectil> proyectiles;

    private BufferedImage playerSprite;
    private BufferedImage zombieSprite;

    public  GamePanel(Player player, Enemy zombie, List<Proyectil> proyectiles) {
        this.player = player;
        this.zombie = zombie;
        this.proyectiles = proyectiles;
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(30,30, 35));
        setFocusable(true);
        loadSprites();
    }

    private void loadSprites() {
        try (InputStream in = getClass().getResourceAsStream("/Resources/imagenes/player_idle.png")) {
            if (in == null) {
                throw new IOException("No se encontró /imagenes/player_idle.png en el classpath");
            }
            playerSprite = ImageIO.read(in);
        } catch (IOException e) {
            System.err.println("Error cargando sprite del jugador: " + e.getMessage());
        }

        try (InputStream in = getClass().getResourceAsStream("/Resources/imagenes/zombie_idle.png")) {
            if (in == null) {
                throw new IOException("No se encontró /imagenes/zombie_idle.png en el classpath");
            }
            zombieSprite = ImageIO.read(in);
        } catch (IOException e) {
            System.err.println("Error cargando sprite del zombie: " + e.getMessage()); 
        }
    }

    public void update() {
        if (player.isAlive()) {
            player.update(getWidth(), getHeight());
            if (zombie.isAlive()) {
                zombie.update(player);
            }
            
            for (Proyectil bala : proyectiles) {
                bala.update(getWidth(), getHeight());
                if (bala.isActivo() && zombie.isAlive() && bala.colisionaCon(zombie)) {
                    bala.setActivo(false); //desaparece la bala
                    zombie.takeDamage((int) bala.getDanio()); // Hace daño al zombie
                    System.out.println("Zombie impactado, vida del zombie: " + zombie.getHealth());
                }
            }
            proyectiles.removeIf(b -> !b.isActivo());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (zombie.isAlive()) {
            if (zombieSprite != null) {
            AffineTransform transformOriginal = g2.getTransform();

            double centroX = zombie.getX() + (zombie.getWidth() / 2.0);
            double centroY = zombie.getY() + (zombie.getHeight() / 2.0);

            g2.translate(centroX, centroY);
            g2.rotate(zombie.getAngulo());

            int w = (int) zombie.getWidth();
            int h = (int) zombie.getHeight();
            g2.drawImage(zombieSprite, -w / 2, -h / 2, w, h, null);

            g2.setTransform(transformOriginal);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 20));
            String textoVida = "HP: " + zombie.getHealth();

            int textoX = (int) zombie.getX();
            int textoY = (int) zombie.getY();
            g2.drawString(textoVida, textoX, textoY);
        } else {

            g2.setColor(Color.RED);
            g2.fillOval((int) zombie.getX(), (int) zombie.getY(), zombie.getWidth(), zombie.getHeight());
        }
    }
        
        g2.setColor(Color.YELLOW);
        for (Proyectil bala : proyectiles) {
            g2.fillOval((int) bala.getX(), (int) bala.getY(), (int) bala.getWidth(), (int) bala.getHeight());
        }
        if (playerSprite != null) {
            AffineTransform transformOriginal = g2.getTransform();

            double centroX = player.getX() + (player.getWidth() / 2.0);
            double centroY = player.getY() + (player.getHeight() / 2.0);

            g2.translate(centroX, centroY);
            g2.rotate(player.getAngulo());

            int w = (int) player.getWidth();
            int h = (int) player.getHeight();
            g2.drawImage(playerSprite, -w / 2, -h / 2, w, h, null);

            g2.setTransform(transformOriginal);
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
