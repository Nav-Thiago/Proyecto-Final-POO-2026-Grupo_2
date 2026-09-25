package view;

import model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList; // [POWERUP]

public class GamePanel extends JPanel {

    private final Player player;
    private final List<Enemy> enemies;
    private final List<Bullet> bullets;
    private final List<WeaponItem> groundWeapons;
    private EnemySpawner spawner;

    // [POWERUP] CopyOnWriteArrayList porque el game loop modifica la lista mientras el hilo de Swing dibuja
    private final List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
    private final PowerUpSpawner powerUpSpawner;

    private BufferedImage playerSprite;
    private BufferedImage zombieSprite;

    private gameStatus status = gameStatus.PLAYING;

    public enum gameStatus {
        PLAYING,
        LEVEL_COMPLETED,
        GAME_OVER,
        GAME_WON
    }

    private Enemy createZombieEnemy(int nivel) {
        return new NormalZombie(600, 300); // por ahora siempre igual, después varía según "nivel"
    }

    private int actualLevel = 1;
    private final int totalLevels = 3;//a modificar(cantidad de niveles que agreguemos a futuro)

    public GamePanel(Player player, List<Enemy> enemies, List<Bullet> bullets, List<WeaponItem> groundWeapons) {
        this.player = player;
        this.enemies = enemies;
        this.bullets = bullets;
        this.groundWeapons = groundWeapons;
        this.spawner = new EnemySpawner(this.enemies, 2000, 10);
        this.powerUpSpawner = new PowerUpSpawner(powerUps, 8000, 3); // [POWERUP] uno cada 8 s, máximo 3 en el mapa
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(30, 30, 35));
        setFocusable(true);
        loadSprites();

    }

    public void playerInteract() {
        if (status != gameStatus.PLAYING) return;

        for (int i = 0; i < this.groundWeapons.size(); i++) {
            WeaponItem item = this.groundWeapons.get(i);

            if (this.player.collidesWith(item)) {
                FireArm oldWeapon = this.player.getPrimaryWeapon();
                this.player.equipWeapon(item.getWeapon());
                this.groundWeapons.remove(i);

                if (oldWeapon != null) {
                    WeaponItem droppedWeapon = new WeaponItem(this.player.getX(), this.player.getY(), oldWeapon);
                    this.groundWeapons.add(droppedWeapon);
                }

                break;
            }
        }
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
        if (status != gameStatus.PLAYING) return;

        if (!player.isAlive()) {
            status = gameStatus.GAME_OVER;
            return;
        }

        player.update(getWidth(), getHeight());

        spawner.update();

        // [POWERUP] spawn y recolección
        powerUpSpawner.update(getWidth(), getHeight());
        for (PowerUp p : powerUps) {
            if (p.isActive() && player.collidesWith(p)) {
                p.collect(player);
            }
        }
        powerUps.removeIf(p -> !p.isActive());

        boolean todosMuertos = true;

        for (Enemy zombie : enemies) {
            if (zombie.isAlive()) {
                todosMuertos = false;
                zombie.update(player);

                for (Bullet bullet : bullets) {
                    bullet.update(getWidth(), getHeight());
                    if (bullet.isActive() && bullet.collidesWith(zombie)) {
                        bullet.setActive(false);
                        zombie.takeDamage((int) bullet.getDamage());
                        System.out.println("Zombie impactado, vida del zombie: " + zombie.getHealth());
                    }
                }
            }
        }

        bullets.removeIf(b -> !b.isActive());

        if (todosMuertos && spawner.isWaveComplete()) {
            if (actualLevel < totalLevels) {
                status = gameStatus.LEVEL_COMPLETED;
            } else {
                status = gameStatus.GAME_WON;
            }
        }

    }

    // [POWERUP] dibujo provisional sin sprites: círculo de color + texto
    private void drawPowerUps(Graphics2D g2) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        for (PowerUp p : powerUps) {
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

        drawPowerUps(g2); // [POWERUP] primero, para que queden debajo de zombies y jugador


        for (Enemy zombie : enemies) {

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

        g2.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g2.fillOval((int) bullet.getX(), (int) bullet.getY(), bullet.getWidth(), bullet.getHeight());
        }
        if (playerSprite != null) {
            AffineTransform transformOriginal = g2.getTransform();

            double centerX = player.getX() + (player.getWidth() / 2.0);
            double centerY = player.getY() + (player.getHeight() / 2.0);

            g2.translate(centerX, centerY);
            g2.rotate(player.getAngle());

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

        // Dibuja el texto en la esquina superior izquierda
        String lifeText = "Supervivencia: " + player.getHealth() + " / " + player.getMaxHealth();

        g2.drawString(lifeText, 20, 30);

        // [POWERUP] indicadores de efectos activos
        if (player.getShield() > 0) {
            g2.setColor(Color.CYAN);
            g2.drawString("Escudo: " + player.getShield(), 20, 55);
        }
        if (player.getDamageBonus() > 0) {
            g2.setColor(Color.RED);
            g2.drawString("Daño +" + Math.round(player.getDamageBonus()), 20, 80);
        }

        if (status == gameStatus.GAME_OVER) {
            showFinalMessage(g2, "JUEGO TERMINADO", Color.RED);
        } else if (status == gameStatus.LEVEL_COMPLETED) {
            showFinalMessage(g2, "¡FELICITACIONES, HAS COMPLETADO EL NIVEL CON ÉXITO!", Color.YELLOW);
        } else if (status == gameStatus.GAME_WON) {
            showFinalMessage(g2, "¡GANASTE!", Color.GREEN);
        }

        // -- Dibujo las armas en el suelo
        for (WeaponItem item : this.groundWeapons) {
            int x = (int) item.getX();
            int y = (int) item.getY();

            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, y, item.getWidth(), item.getHeight());

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            String weaponName = item.getWeapon().getClass().getSimpleName();
            g2.drawString(weaponName, x,  y - 5);
        }

        // Dibujar munición y arma activa
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        Weapon activeWeapon = player.getActiveWeapon();

        if (activeWeapon instanceof FireArm) {
            FireArm fa = (FireArm) activeWeapon;
            String ammoText = "Arma: " + fa.getClass().getSimpleName() + " - Balas: " + fa.getActualMunition() + " / " + fa.getMaxMunition();
            g2.setColor(Color.WHITE);
            // Lo dibujamos debajo de los textos de Vida, Escudo y Daño
            g2.drawString(ammoText, 20, 105);
        } else if (activeWeapon instanceof MeleeWeapon) {
            g2.setColor(Color.WHITE);
            g2.drawString("Arma: Cuchillo", 20, 105);
        }
    }

    public void nextLevel() {
        if (status != gameStatus.LEVEL_COMPLETED) return;

        actualLevel++;
        enemies.clear();
        enemies.add(createZombieEnemy(actualLevel));
        bullets.clear();
        powerUps.clear(); // [POWERUP]
        status = gameStatus.PLAYING;
    }
}
