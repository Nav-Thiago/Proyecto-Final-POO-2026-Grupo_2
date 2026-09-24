package view;

import model.Enemy;
import model.Player;
import model.Bullet;
import model.NormalZombie;
import model.PowerUp;          // [POWERUP]
import model.PowerUpSpawner;   // [POWERUP]
import model.GameModel;

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

    private final Player player;
    private final List<Enemy> enemies;
    private final List<Bullet> bullets;
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

    public  GamePanel(Player player, List<Enemy> enemies, List<Bullet> bullets) {
        this.player = player;
        this.enemies = enemies;
        this.bullets = bullets;
        this.spawner = new EnemySpawner(this.enemies, 2000, 10);
        this.powerUpSpawner = new PowerUpSpawner(powerUps, 8000, 3); // [POWERUP] uno cada 8 s, máximo 3 en el mapa


public class GamePanel extends JPanel {

    private final GameModel model; // La única dependencia estructural
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

            if (in != null) zombieSprite = ImageIO.read(in);
        } catch (IOException e) { System.err.println("Error zombie: " + e.getMessage()); }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        drawPowerUps(g2); // [POWERUP] primero, para que queden debajo de zombies y jugador
      
        
        for (Enemy zombie : enemies) {
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
        g2.drawString("Supervivencia: " + player.getHealth() + " / " + player.getMaxHealth(), 20, 30);

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
        if (currentStatus == GameModel.gameStatus.GAME_OVER) {
            showFinalMessage(g2, "JUEGO TERMINADO", Color.RED);
        } else if (currentStatus == GameModel.gameStatus.LEVEL_COMPLETED) {
            showFinalMessage(g2, "¡FELICITACIONES, HAS COMPLETADO EL NIVEL CON ÉXITO!", Color.YELLOW);
        } else if (currentStatus == GameModel.gameStatus.GAME_WON) {
            showFinalMessage(g2, "¡GANASTE!", Color.GREEN);
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
