package view;

import controller.GameController;
import model.Enemy;
import model.FinalBoss;
import view.hud.HudData;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.HashMap;

import java.awt.geom.Area;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import view.hud.Hud;

public class GamePanel extends JPanel {

    private final GameController controller;

    private Animation playerFeetAnimation;
    private final Map<String, Animation> playerAnimations;
    
    private BufferedImage backgroundForest;
    private BufferedImage firstLayer;

    // Mapas para asociar la clase del modelo con su recurso visual
    private final Map<String, Animation> zombieAnimations;
    private final Map<String, BufferedImage> zombieIdleSprites;
    private final Map<String, Animation> zombieAttackAnimations;
    private final Map<String, Animation> zombieSpitAnimations ;
    private final Map<String, BufferedImage> powerUpSprites;
    private final Map<String, BufferedImage> weaponSprites;

    private static final Map<String, Double> SPRITE_ROTATION = Map.of("FastZombie", Math.PI / 2, "PoisonZombie", Math.PI / 2, "TankZombie", -Math.PI / 2, "FinalBoss", -Math.PI / 2);
    private static final Map<String, Double> ATTACK_SCALES = Map.of("FinalBoss", 2.0);

    private static final Map<String, Color> POWERUP_COLORS = Map.of("Heal", Color.GREEN, "Shield", Color.CYAN, "DamageBoost", Color.RED, "Ammo", Color.YELLOW, "MaxAmmo", Color.ORANGE);
    private static final Map<String, String> POWERUP_LABELS = Map.of("Heal", "HP", "Shield", "SH", "DamageBoost", "+25", "Ammo", "AMMO", "MaxAmmo", "MAX");

    private final BulletEffects bulletEffects = new BulletEffects();

    private final Hud hud = new Hud();

    private Graphics2D g2;

    public GamePanel(GameController controller, GameAssets assets) {
        this.controller = controller;
        this.backgroundForest = assets.getBackgroundForest();
        this.firstLayer = assets.getFirstLayer();
        this.playerFeetAnimation = assets.getPlayerFeetAnimation();
        this.playerAnimations = assets.getPlayerAnimations();
        this.zombieAnimations = assets.getZombieAnimations();
        this.zombieIdleSprites = assets.getZombieIdleSprites();
        this.zombieAttackAnimations = assets.getZombieAttackAnimations();
        this.zombieSpitAnimations = assets.getZombieSpitAnimations();
        this.powerUpSprites = assets.getPowerUpSprites();
        this.weaponSprites = assets.getWeaponSprites();

        setPreferredSize(new Dimension(1920, 1080));
        setFocusable(true);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        this.controller.drawScene(this);

    }

    // Dibuja al jugador
    public void drawPlayer(double x, double y, int w, int h, double angle, boolean moving, String weaponType) {
        AffineTransform transformOriginal = g2.getTransform();

        double centerX = x + (w / 2.0);
        double centerY = y + (h / 2.0);

        g2.translate(centerX, centerY);
        g2.rotate(angle);

        if (moving && playerFeetAnimation != null && playerFeetAnimation.getCurrentFrame() != null) {
            BufferedImage feetFrame = playerFeetAnimation.getCurrentFrame();
            AffineTransform transformFeet = g2.getTransform();
            g2.scale(0.6, 0.6);
            g2.drawImage(feetFrame, -w / 2, -h / 2, w, h, null);
            g2.setTransform(transformFeet);
        }

        Animation currentAnim = playerAnimations.get(weaponType);

        if (currentAnim != null) {
            currentAnim.update();
            if (currentAnim.getCurrentFrame() != null) {
                BufferedImage bodyFrame = currentAnim.getCurrentFrame();
                g2.drawImage(bodyFrame, -w / 2, -h / 2, w, h, null);
            }
        } else {
            g2.setColor(Color.RED);
            g2.fillOval(-w / 2, -h / 2, w, h);
        }

        g2.setTransform(transformOriginal);
    }

    // Dibuja al zombie
    // NOTA: Se agregó el parámetro 'boolean isAttacking'
    public void drawZombie(double x, double y, int w, int h, double angle, int health, String type, boolean isAttacking, boolean isSpitting) {
        double spriteAngle = angle + SPRITE_ROTATION.getOrDefault(type, 0.0);

        AffineTransform transformOriginal = g2.getTransform();
        double centerX = x + (w / 2.0);
        double centerY = y + (h / 2.0);

        g2.translate(centerX, centerY);
        g2.rotate(spriteAngle);

        Animation spitAnim = zombieSpitAnimations.get(type);
        Animation attackAnim = zombieAttackAnimations.get(type);
        Animation walkAnim = zombieAnimations.get(type);
        BufferedImage idle = zombieIdleSprites.get(type);

        BufferedImage frameToDraw = null;
        double scale = 1.0;

        // Lógica de prioridad: Escupir -> Ataque -> Caminar -> Reposo -> Círculo rojo de error
        if (isSpitting && spitAnim != null && spitAnim.getCurrentFrame() != null) {
            frameToDraw = spitAnim.getCurrentFrame();
            scale = ATTACK_SCALES.getOrDefault(type, 1.0);
        } else if (isAttacking && attackAnim != null && attackAnim.getCurrentFrame() != null) {
            frameToDraw = attackAnim.getCurrentFrame();
            scale = ATTACK_SCALES.getOrDefault(type, 1.0);
        } else if (walkAnim != null && walkAnim.getCurrentFrame() != null) {
            frameToDraw = walkAnim.getCurrentFrame();
        } else if (idle != null) {
            frameToDraw = idle;
        }

        if (frameToDraw != null) {
            double imgW = frameToDraw.getWidth();
            double imgH = frameToDraw.getHeight();
            double ratio = imgW / imgH;

            int drawH = (int) (h * scale);
            int drawW = (int) (drawH * ratio); 
            
            g2.drawImage(frameToDraw, -drawW / 2, -drawH / 2, drawW, drawH, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillOval(-w / 2, -h / 2, w, h);
        }

        g2.setTransform(transformOriginal);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.drawString("HP: " + health, (int) x, (int) y);
    }

    public void updateZombieAnimations() {
        for (Animation anim : zombieAnimations.values()) {
            anim.update();
        }
        for (Animation anim : zombieAttackAnimations.values()) {
            anim.update();
        }
        for (Animation anim : zombieSpitAnimations.values()) {
        anim.update();
        }
    }

    // Dibuja los power-up
    public void drawPowerUp(double x, double y, int w, int h, String type) {
        BufferedImage sprite = powerUpSprites.get(type);
        Color color = POWERUP_COLORS.getOrDefault(type, Color.WHITE);
        String label = POWERUP_LABELS.getOrDefault(type, "?");

        drawFloating(g2, x, y, w, h, color, g -> {
            if (sprite != null) {
                g.drawImage(sprite, -w / 2, -h / 2, w, h, null);
            } else {
                g.setColor(color);
                g.fillOval(-w / 2, -h / 2, w, h);
                g.setColor(Color.BLACK);
                g.drawOval(-w / 2, -h / 2, w, h);
                g.setFont(new Font("Monospaced", Font.BOLD, 11));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(label, -fm.stringWidth(label) / 2, fm.getAscent() / 2 - 2);
            }
        });
    }

    // Dibuja las armas
    public void drawGroundWeapon(double x, double y, int w, int h, String type) {
        BufferedImage sprite = weaponSprites.get(type);
        int drawX = (int) x;
        int drawY = (int) y;

        if (sprite != null) {
            g2.drawImage(sprite, drawX, drawY, w, h, null);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(drawX, drawY, w, h);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g2.drawString(type, drawX, drawY - 5);
        }
    }

    // Dibuja balas de veneno
    // Dibuja balas de veneno (solo el círculo verde)
    public void drawPoisonBullet(double x, double y, int w, int h) {
        g2.setColor(new Color(50, 205, 50));
        g2.fillOval((int) x, (int) y, w, h);
    }

    // Primera capa
    public void drawFirstLayer(int mapWidth, int mapHeight) {
        if (firstLayer != null) {
            g2.drawImage(firstLayer, 0, 0, mapWidth, mapHeight, null);
        }
    }

    // Mapa
    public void drawBackground(int mapWidth, int mapHeight) {
        if (backgroundForest != null) {
            g2.drawImage(backgroundForest, 0, 0, mapWidth, mapHeight, null);
        }
    }

    // Oscuridad
    public void drawDarkness(double playerX, double playerY, int playerW, int playerH, double playerAngle, int mapWidth, int mapHeight) {
        double centerX = playerX + (playerW / 2.0);
        double centerY = playerY + (playerH / 2.0);

        Area darkness = new Area(new Rectangle2D.Double(0, 0, mapWidth, mapHeight));

        double lightRadius = 700.0; // Define distancia
        double fovAngle = 85.0; // Define ángulo

        double angleDegrees = -Math.toDegrees(playerAngle);

        Arc2D.Double lightCone = new Arc2D.Double(
                centerX - lightRadius, centerY - lightRadius,
                lightRadius * 2, lightRadius * 2,
                angleDegrees - (fovAngle / 2.0), fovAngle,
                Arc2D.PIE
        );

        double personalRadius = 90.0; // Círculo personal donde se va a ver
        Ellipse2D.Double personalCircle = new Ellipse2D.Double(
                centerX - personalRadius, centerY - personalRadius,
                personalRadius * 2, personalRadius * 2
        );

        darkness.subtract(new Area(lightCone)); // Substrae el cono de iluminación
        darkness.subtract(new Area(personalCircle)); // Substrae el círculo personal

        g2.setColor(new Color(0, 0, 0, 220)); // 245 = opacidad
        g2.fill(darkness);

        g2.setStroke(new BasicStroke(1.0f)); // Restaurar el grosor normal
    }

    // HUD
    
       public void drawHud(HudData data) { hud.draw(g2, data, getWidth(), getHeight()); }
public void onPowerUpPickup(String type) { hud.onPickup(type); }

    // Pantalla de intro del jefe final
    public void drawBossIntro(int step, double progress) {
        // el texto aparece y se desvanece de a poco
        double fade = Math.max(0.0, Math.min(1.0, Math.min(progress / 0.25, (1.0 - progress) / 0.25)));
        int alpha = (int) (255 * fade);

        // fondo oscuro
        g2.setColor(new Color(0, 0, 0, 215));
        g2.fillRect(0, 0, getWidth(), getHeight());

        String text;
        Color color;
        int size;
        if (step == 0) {
            text = "Felicidades, sobreviviste a la primer oleada, buen trabajo";
            color = new Color(190, 190, 190);
            size = 32;
        } else {
            text = "Preparate para el Jefe";
            color = new Color(190, 0, 0);
            size = 54;
        }

        // si el texto no entra en pantalla, se achica la letra
        Font font = new Font("Serif", Font.BOLD | Font.ITALIC, size);
        FontMetrics fm = g2.getFontMetrics(font);
        while (fm.stringWidth(text) > getWidth() - 80 && size > 12) {
            size--;
            font = new Font("Serif", Font.BOLD | Font.ITALIC, size);
            fm = g2.getFontMetrics(font);
        }
        g2.setFont(font);

        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2;

        // temblor leve solo en "Prepárate para el Jefe"
        if (step == 1) {
            x += (int) (Math.random() * 5) - 2;
            y += (int) (Math.random() * 5) - 2;
        }

        // sombra rojo oscuro y texto encima
        g2.setColor(new Color(70, 0, 0, alpha));
        g2.drawString(text, x + 3, y + 3);
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g2.drawString(text, x, y);
    }

    // Mensaje en pantalla
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

    private String formatTime(long ms) {
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void drawCenterText(Graphics2D g2, String text, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, y);
    }

    private void drawTime(Graphics2D g2, long matchTimeMs) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        int y = getHeight() / 2 + 50;

        g2.setColor(Color.WHITE);
        drawCenterText(g2, "Tu tiempo: " + formatTime(matchTimeMs), y);
    }

    private void drawRecord(Graphics2D g2, long bestTimeMs, boolean newRecord) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        int y = getHeight() / 2 + 85;

        g2.setColor(Color.WHITE);
        drawCenterText(g2, "Mejor tiempo: " + formatTime(bestTimeMs), y);

        if (newRecord) {
            g2.setColor(Color.YELLOW);
            drawCenterText(g2, "¡NUEVO RÉCORD!", y + 45);
        }
    }

    // Mensajes de estado
    public void drawGameOver(long matchTimeMs) {
        showFinalMessage(g2,"JUEGO TERMINADO", Color.RED);
        drawTime(g2, matchTimeMs);
    }

    public void drawGameWon(long matchTimeMs, long bestTimeMS, boolean newRecord) {
        showFinalMessage(g2,"¡GANASTE!", Color.GREEN);
        drawTime(g2, matchTimeMs);
        drawRecord(g2, bestTimeMS, newRecord);
    }

    public void drawPause() {
        showFinalMessage(g2,"PAUSA (ESC) para continuar)", Color.WHITE);
    }

    // Efectos para los power-ups
    private void drawFloating(Graphics2D g2, double x, double y, int w, int h, Color glowColor, Consumer<Graphics2D> content) {
        double t = System.currentTimeMillis() / 1000.0;
        double phase = x * 0.05;                               // desfase por objeto
        double bob = Math.sin(t * 3.0 + phase) * 4;                   // sube y baja 4 px
        double scale = 1.0 + Math.sin(t * 4.0 + phase) * 0.06;        // pulso de +-6%
        int glowAlpha = (int) (70 + Math.sin(t * 4.0 + phase) * 40);  // brillo 30..110

        AffineTransform old = g2.getTransform();
        g2.translate(x + w / 2.0, y + h / 2.0 + bob);

        g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), glowAlpha));
        g2.fillOval(-w / 2 - 6, -h / 2 - 6, w + 12, h + 12);

        g2.scale(scale, scale);
        content.accept(g2);       // cada uno dibuja lo suyo, centrado en (0, 0)

        g2.setTransform(old);
    }

    // Efectos de la bala
    public void drawBulletTracer(double x, double y, int w, int h, double dirX, double dirY, double speed, int age, String kind) {
        bulletEffects.drawTracer(g2, x, y, w, h, dirX, dirY, speed, age, kind);
    }

    public void drawMuzzleFlash(double x, double y, int w, int h, double angle, String weaponType, long lastShoot, double muzzleForward, double muzzleSide) {
        bulletEffects.drawMuzzleFlash(g2, x, y, w, h, angle, weaponType, lastShoot, muzzleForward, muzzleSide);
    }

    // Camara de seguimiento
    public void applyCamera(int camX, int camY) {
        g2.translate(-camX, -camY);
    }

    public void resetCamera(double camX, double camY) {
        g2.translate(camX, camY);
    }
}