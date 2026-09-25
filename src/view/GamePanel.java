package view;

import model.Enemy;
import model.Player;
import model.Bullet;
import model.GameModel;
import model.PowerUp; // Importante para que dibuje los powerups

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GamePanel extends JPanel {

    private final GameModel model; 
    private BufferedImage playerSprite;
    private BufferedImage zombieSprite;

    public GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(30, 30, 35));
        setFocusable(true);
        loadSprites();
    }

    private void loadSprites() {
        try (InputStream in = getClass().getResourceAsStream("/Resources/imagenes/player_idle.png")) {
            if (in != null) playerSprite = ImageIO.read(in);
        } catch (IOException e) { System.err.println("Error jugador: " + e.getMessage()); }

        try (InputStream in = getClass().getResourceAsStream("/Resources/imagenes/zombie_idle.png")) {
            if (in != null) zombieSprite = ImageIO.read(in);
        } catch (IOException e) { System.err.println("Error zombie: " + e.getMessage()); }
    }

    // [POWERUP] Dibujado de los potenciadores
    private void drawPowerUps(Graphics2D g2) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        for (PowerUp p : model.getPowerUps()) {
            int x = (int) p.getX();
            int y = (int) p.getY();
            g2.setColor(p.getColor());
            g2.fillOval(x, y, p.getWidth(), p.getHeight());
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, p.getWidth(), p.getHeight());
            String label = p.getLabel();
            g2.drawString(label,
                    x + (p.getWidth() - fm.stringWidth(label)) / 2,
                    y + (p.getHeight() + fm.getAscent()) / 2 - 2);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // [POWERUP] Dibujamos los potenciadores debajo de los personajes
        drawPowerUps(g2); 

        GameModel.gameStatus currentStatus = model.getStatus();
        Player player = model.getPlayer();

        if (currentStatus == GameModel.gameStatus.PLAYING || currentStatus == GameModel.gameStatus.GAME_OVER) {
            
            for (Enemy zombie : model.getEnemies()) {
                if (zombieSprite != null) {
                    AffineTransform transformOriginal = g2.getTransform();
                    double centerX = zombie.getX() + (zombie.getWidth() / 2.0);
                    double centerY = zombie.getY() + (zombie.getHeight() / 2.0);

                    g2.translate(centerX, centerY);
                    g2.rotate(zombie.getAngle());
                    int w = (int) zombie.getWidth();
                    int h = (int) zombie.getHeight();
                    g2.drawImage(zombieSprite, -w / 2, -h / 2, w, h, null);
                    g2.setTransform(transformOriginal);
                } else {
                    g2.setColor(Color.RED);
                    g2.fillOval((int) zombie.getX(), (int) zombie.getY(), zombie.getWidth(), zombie.getHeight());
                }

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.BOLD, 20));
                g2.drawString("HP: " + zombie.getHealth(), (int) zombie.getX(), (int) zombie.getY());
            }

            g2.setColor(Color.GREEN);
            for (Bullet poison : model.getPoisonBullets()) {
                g2.fillOval((int) poison.getX(), (int) poison.getY(), poison.getWidth(), (int) poison.getHeight());
            }

            g2.setColor(Color.YELLOW);
            for (Bullet bullet : model.getPlayerBullets()) {
                g2.fillOval((int) bullet.getX(), (int) bullet.getY(), bullet.getWidth(), bullet.getHeight());
            }
        }

        if (playerSprite != null) {
            AffineTransform transformOriginal = g2.getTransform();
            double centerX = player.getX() + (player.getWidth() / 2.0);
            double centerY = player.getY() + (player.getHeight() / 2.0);
            g2.translate(centerX, centerY);
            g2.rotate(player.getAngle());
            g2.drawImage(playerSprite, -(int)player.getWidth() / 2, -(int)player.getHeight() / 2, (int)player.getWidth(), (int)player.getHeight(), null);
            g2.setTransform(transformOriginal);
        } else {
            g2.setColor(Color.BLUE);
            g2.fillOval((int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight());
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        String lifeText = "Supervivencia: " + player.getHealth() + " / " + player.getMaxHealth();
        g2.drawString(lifeText, 20, 30);

        // [POWERUP] Indicadores de efectos activos
        if (player.getShield() > 0) {
            g2.setColor(Color.CYAN);
            g2.drawString("Escudo: " + player.getShield(), 20, 55);
        }
        if (player.getDamageBonus() > 0) {
            g2.setColor(Color.RED);
            g2.drawString("Daño +" + Math.round(player.getDamageBonus()), 20, 80);
        }

        if (currentStatus == GameModel.gameStatus.GAME_OVER) {
            showFinalMessage(g2, "JUEGO TERMINADO", Color.RED);
        } else if (currentStatus == GameModel.gameStatus.LEVEL_COMPLETED) {
            showFinalMessage(g2, "¡FELICITACIONES, HAS COMPLETADO EL NIVEL CON ÉXITO!", Color.YELLOW);
        } else if (currentStatus == GameModel.gameStatus.GAME_WON) {
            showFinalMessage(g2, "¡GANASTE!", Color.GREEN);
        }
    }

    private void showFinalMessage(Graphics2D g2, String texto, Color color) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(color);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(texto)) / 2;
        int textY = getHeight() / 2;
        g2.drawString(texto, textX, textY);
    }
}

