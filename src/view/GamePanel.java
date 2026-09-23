package view;

import model.Enemy;
import model.EnemySpawner;
import model.Player;
import model.Bullet;
import model.NormalZombie;

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
    private final List<Enemy> enemies;
    private final List<Bullet> bullets;
    private EnemySpawner spawner;


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
        if (status != gameStatus.PLAYING) return;

        if (!player.isAlive()) {
            status = gameStatus.GAME_OVER;
            return;
        }

        player.update(getWidth(), getHeight());

        spawner.update();

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

        if (status == gameStatus.GAME_OVER) {
            showFinalMessage(g2, "JUEGO TERMINADO", Color.RED);
        } else if (status == gameStatus.LEVEL_COMPLETED) {
            showFinalMessage(g2, "¡FELICITACIONES, HAS COMPLETADO EL NIVEL CON ÉXITO!", Color.YELLOW);
        } else if (status == gameStatus.GAME_WON) {
            showFinalMessage(g2, "¡GANASTE!", Color.GREEN);
        }
    }

        public void nextLevel() {
            if (status != gameStatus.LEVEL_COMPLETED) return;

            actualLevel++;
            enemies.clear();
            enemies.add(createZombieEnemy(actualLevel));
            bullets.clear();
            status = gameStatus.PLAYING;
        }
}

