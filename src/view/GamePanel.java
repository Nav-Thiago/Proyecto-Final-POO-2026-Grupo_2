package view;


import model.*;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList; // [POWERUP]


public class GamePanel extends JPanel {


    private final GameModel model;


    private BufferedImage playerSprite;
    private BufferedImage zombieSprite;

    public  GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(30,30, 35));
        setFocusable(true);
        loadSprites();
    }


    public void playerInteract() {
        this.model.playerInteract();
    }


    public void nextLevel() {
        this.model.nextLevel();
    }


    private void loadSprites() {
        try (InputStream in = getClass().getResourceAsStream("/Resources/imagenes/player_idle.png")) {
            if (in != null) {
                playerSprite = ImageIO.read(in);
            }
        } catch (IOException e) {
            System.err.println("Error cargando sprite del jugador: " + e.getMessage());
        }


        try (InputStream in = getClass().getResourceAsStream("/Resources/imagenes/zombie_idle.png")) {
            if (in != null) {
                zombieSprite = ImageIO.read(in);
            }
        } catch (IOException e) {
            System.err.println("Error cargando sprite del zombie: " + e.getMessage());
        }
    }


    // [POWERUP] dibujo provisional sin sprites: círculo de color + texto
    private void drawPowerUps(Graphics2D g2) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        for (PowerUp p : this.model.getPowerUps()) {
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
    
    public void togglePause() {
        this.model.togglePause();
    }


    private void showFinalMessage(Graphics2D g2, String texto, Color color) {
        g2.setColor(new Color(0, 0, 0, 180)); // fondo semitransparente
        g2.fillRect(0, 0, getWidth(), getHeight());


        g2.setColor(color);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(texto)) / 2;
        int textY = getHeight() / 2;
        g2.drawString(texto, textX, textY);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);


        Player player = this.model.getPlayer();


        // Dibuja PowerUps
        drawPowerUps(g2); // [POWERUP] primero, para que queden debajo de zombies y jugador


        // Dibuja Armas en el suelo
        for (WeaponItem item : this.model.getGroundWeapons()) {
            int x = (int) item.getX();
            int y = (int) item.getY();


            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, y, item.getWidth(), item.getHeight());


            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            String weaponName = item.getWeapon().getClass().getSimpleName();
            g2.drawString(weaponName, x, y - 5);
        }


        // Dibuja a los enemigos
        for (Enemy zombie : this.model.getEnemies()) {


            if (zombie.isAlive()) {
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
                String lifeText = "HP: " + zombie.getHealth();


                int textX = (int) zombie.getX();
                int textY = (int) zombie.getY();
                g2.drawString(lifeText, textX, textY);
            }
        }


        // Dibuja las balas del jugador
        g2.setColor(Color.YELLOW);
        for (Bullet bullet : this.model.getPlayerBullets()) {
            g2.fillOval((int) bullet.getX(), (int) bullet.getY(), bullet.getWidth(), bullet.getHeight());
        }


        // Dibuja balas de veneno
        g2.setColor(new Color(50, 205, 50));
        for (Bullet poison : this.model.getPoisonBullets()) {
            g2.fillOval((int) poison.getX(), (int) poison.getY(), poison.getWidth(), poison.getHeight());
        }


        // Dibuja al jugador
        if (playerSprite != null) {
            AffineTransform transformOriginal = g2.getTransform();


            double centerX = player.getX() + (player.getWidth() / 2.0);
            double centerY = player.getY() + (player.getHeight() / 2.0);


            g2.translate(centerX, centerY);
            g2.rotate(player.getAngle());
            g2.drawImage(playerSprite, -player.getWidth() / 2, -player.getHeight() / 2, player.getWidth(), player.getHeight(), null);
            g2.setTransform(transformOriginal);


        } else {
            g2.setColor(Color.BLUE);
            g2.fillOval((int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight());
        }


        // Dibuja la interfaz
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20)); // Configura la tipografía
        g2.drawString("Supervivencia: " + player.getHealth() + " / " + player.getMaxHealth(), 20, 30); // Dibuja el texto en la esquina superior izquierda


        if (player.getShield() > 0) {
            g2.setColor(Color.CYAN);
            g2.drawString("Escudo: " + player.getShield(), 20, 55);
        }
        if (player.getDamageBonus() > 0) {
            g2.setColor(Color.RED);
            g2.drawString("Daño +" + Math.round(player.getDamageBonus()), 20, 80);
        }


        // Texto de arma activa y munición
        Weapon activeWeapon = player.getActiveWeapon();
        if (activeWeapon instanceof FireArm fa) {
            String ammoText = "Arma: " + fa.getClass().getSimpleName() + " - Balas: " + fa.getActualMunition() + " / " + fa.getMaxMunition();
            g2.setColor(Color.WHITE);
            g2.drawString(ammoText, 20, 105);
        } else if (activeWeapon instanceof MeleeWeapon) {
            g2.setColor(Color.WHITE);
            g2.drawString("Arma: Cuchillo", 20, 105);
        }


        // Mensaje de estados
        GameModel.gameStatus status = model.getStatus();
        if (status == GameModel.gameStatus.GAME_OVER) {
            showFinalMessage(g2, "JUEGO TERMINADO", Color.RED);
        } else if (status == GameModel.gameStatus.LEVEL_COMPLETED) {
            showFinalMessage(g2, "¡FELICITACIONES, HAS COMPLETADO EL NIVEL CON ÉXITO!", Color.YELLOW);
        } else if (status == GameModel.gameStatus.GAME_WON) {
            showFinalMessage(g2, "¡GANASTE!", Color.GREEN);
        } else if (status == GameModel.gameStatus.PAUSED) {
            showFinalMessage(g2, "PAUSA (ESC) para continuar)", Color.WHITE);
        }

        // HITBOXES
        g2.setStroke(new BasicStroke(2.0f)); // Líneas más gruesas para que se vean bien

            // Hitbox del jugador (Verde)
            g2.setColor(Color.GREEN);
            Rectangle pBounds = player.getBounds();
            g2.drawRect(pBounds.x, pBounds.y, pBounds.width, pBounds.height);

            // Hitboxes de los zombies (Rojo)
            g2.setColor(Color.RED);
            for (Enemy zombie : this.model.getEnemies()) {
                if (zombie.isAlive()) {
                    Rectangle zBounds = zombie.getBounds();
                    g2.drawRect(zBounds.x, zBounds.y, zBounds.width, zBounds.height);
                }
            }

            // Hitboxes de las balas (Cian)
            g2.setColor(Color.CYAN);
            for (Bullet bullet : this.model.getPlayerBullets()) {
                if (bullet.isActive()) {
                    Rectangle bBounds = bullet.getBounds();
                    g2.drawRect(bBounds.x, bBounds.y, bBounds.width, bBounds.height);
                }
            }
            
            g2.setStroke(new BasicStroke(1.0f)); // Restaurar el grosor normal
    }
}