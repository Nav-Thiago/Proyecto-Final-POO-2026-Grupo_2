package view;

import controller.GameController;
import view.hud.HudData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.Consumer;

import view.hud.Hud;

public class GamePanel extends JPanel {

    private final GameController controller;

    private final Animation playerFeetAnimation;
    private final Map<String, Animation> playerAnimations;
    private final Map<String, Animation> playerAttackAnimations;

    private final BufferedImage backgroundForest;
    private final BufferedImage firstLayer;

    // Mapas para asociar la clase del modelo con su recurso visual
    private final Map<String, Animation> zombieAnimations;
    private final Map<String, BufferedImage> zombieIdleSprites;
    private final Map<String, Animation> zombieAttackAnimations;
    private final Map<String, Animation> zombieSpitAnimations;
    private final Map<String, BufferedImage> powerUpSprites;
    private final Map<String, BufferedImage> weaponSprites;

    private static final Map<String, Double> SPRITE_ROTATION = Map.of("FAST", Math.PI / 2, "POISON",
            Math.PI / 2, "TANK", -Math.PI / 2, "BOSS", -Math.PI / 2);
    private static final Map<String, Double> ATTACK_SCALES = Map.of("BOSS", 2.0);

    private static final Map<String, Color> POWERUP_COLORS = Map.of("Heal", Color.GREEN, "Shield", Color.CYAN,
            "DamageBoost", Color.RED, "Ammo", Color.YELLOW, "MaxAmmo", Color.ORANGE);
    private static final Map<String, String> POWERUP_LABELS = Map.of("Heal", "HP", "Shield", "SH", "DamageBoost", "+25",
            "Ammo", "AMMO", "MaxAmmo", "MAX");

    private final BulletEffects bulletEffects = new BulletEffects();
    private final VisionConeRenderer visionConeRenderer = new VisionConeRenderer();

    private final Hud hud = new Hud();

    private final PoisonEffects poisonEffects = new PoisonEffects();

    private Graphics2D g2;



    public GamePanel(GameController controller, GameAssets assets) {
        this.controller = controller;
        this.backgroundForest = assets.getBackgroundForest();
        this.firstLayer = assets.getFirstLayer();
        this.playerFeetAnimation = assets.getPlayerFeetAnimation();
        this.playerAnimations = assets.getPlayerAnimations();
        this.playerAttackAnimations = assets.getPlayerAttackAnimations();
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
    // swingProgress: avance (0.0 a 1.0) del golpe cuerpo a cuerpo; -1 si no está golpeando
    public void drawPlayer(double x, double y, int w, int h, double angle, boolean moving, String weaponType,
            double swingProgress) {
        AffineTransform transformOriginal = g2.getTransform();

        double centerX = x + (w / 2.0);
        double centerY = y + (h / 2.0);

        g2.translate(centerX, centerY);
        g2.rotate(angle);

        boolean swinging = swingProgress >= 0;

        // Los pies no se dibujan durante el golpe: el sprite de ataque ya trae el cuerpo
        // en otra posición y los pies se verían flotando.
        if (moving && !swinging && playerFeetAnimation != null && playerFeetAnimation.getCurrentFrame() != null) {
            BufferedImage feetFrame = playerFeetAnimation.getCurrentFrame();
            AffineTransform transformFeet = g2.getTransform();
            g2.scale(0.6, 0.6);
            g2.drawImage(feetFrame, -w / 2, -h / 2, w, h, null);
            g2.setTransform(transformFeet);
        }

        Animation currentAnim = playerAnimations.get(weaponType);
        Animation attackAnim = playerAttackAnimations.get(weaponType);

        if (swinging && attackAnim != null && attackAnim.getFrameAt(swingProgress) != null) {
            BufferedImage attackFrame = attackAnim.getFrameAt(swingProgress);

            // Misma escala por eje que usa el sprite de caminar (se dibuja a w x h),
            // así el cuerpo mantiene el mismo tamaño en pantalla durante el golpe.
            double sx = 1.0;
            double sy = 1.0;
            if (currentAnim != null && currentAnim.getCurrentFrame() != null) {
                BufferedImage ref = currentAnim.getCurrentFrame();
                sx = w / (double) ref.getWidth();
                sy = h / (double) ref.getHeight();
            }
            sx *= attackAnim.getScale();
            sy *= attackAnim.getScale();

            int drawW = (int) (attackFrame.getWidth() * sx);
            int drawH = (int) (attackFrame.getHeight() * sy);

            g2.drawImage(attackFrame,
                    (int) (-drawW / 2.0 + attackAnim.getOffsetX()),
                    (int) (-drawH / 2.0 + attackAnim.getOffsetY()),
                    drawW, drawH, null);

        } else if (currentAnim != null) {
            currentAnim.update();
            if (currentAnim.getCurrentFrame() != null) {
                g2.drawImage(currentAnim.getCurrentFrame(), -w / 2, -h / 2, w, h, null);
            }
        } else {
            g2.setColor(Color.RED);
            g2.fillOval(-w / 2, -h / 2, w, h);
        }

        g2.setTransform(transformOriginal);
    }

        // Dibuja al zombie
    // attackProgress: avance (0.0 a 1.0) de la animación de ataque; -1 si no está atacando
    // spitProgress: avance (0.0 a 1.0) de la animación de escupir; -1 si no está escupiendo
    public void drawZombie(double x, double y, int w, int h, double angle, int health, String type,
            double attackProgress, double spitProgress) {
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
        if (spitProgress >= 0 && spitAnim != null && spitAnim.getFrameAt(spitProgress) != null) {
            frameToDraw = spitAnim.getFrameAt(spitProgress);

            // Escala relativa al sprite de caminar: el cuerpo conserva el mismo tamaño en pantalla
            BufferedImage ref = (walkAnim != null) ? walkAnim.getCurrentFrame() : null;
            if (ref != null) {
                scale = frameToDraw.getHeight() / (double) ref.getHeight();
            }
        } else if (attackProgress >= 0 && attackAnim != null && attackAnim.getFrameAt(attackProgress) != null) {
            frameToDraw = attackAnim.getFrameAt(attackProgress);
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

    // Avanzan solo las animaciones que van en bucle (caminar).
    // Las de ataque y escupir no: se eligen por progreso desde el modelo.
    public void updateZombieAnimations() {
        for (Animation anim : zombieAnimations.values()) {
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
    public void drawPoisonBullet(double x, double y, int w, int h, double dirX, double dirY, double speed, int age, String type) {
        if (type.equals("BossSpit")) {
            poisonEffects.drawBossSpit(g2, x, y, w, h, dirX, dirY, speed, age);
        } else {
        // Bala normal: solo el círculo verde
            g2.setColor(new Color(50, 205, 50));
            g2.fillOval((int) x, (int) y, w, h);
        }
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

    // Oscuridad (delegada a la clase "VisionConeRenderer" con bordes suaves)
    public void drawDarkness(double playerX, double playerY, int playerW, int playerH, double playerAngle, int mapWidth,
            int mapHeight) {
        visionConeRenderer.renderDarkness(g2, playerX, playerY, playerW, playerH, playerAngle, getWidth(), getHeight(),
                camX, camY);
    }

    // HUD
    public void drawHud(HudData data) {
        hud.draw(g2, data, getWidth(), getHeight());
    }

    public void onPowerUpPickup(String type) {
        hud.onPickup(type);
    }

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

    // Efectos para los power-ups
    private void drawFloating(Graphics2D g2, double x, double y, int w, int h, Color glowColor,
            Consumer<Graphics2D> content) {
        double t = System.currentTimeMillis() / 1000.0;
        double phase = x * 0.05; // desfase por objeto
        double bob = Math.sin(t * 3.0 + phase) * 4; // sube y baja 4 px
        double scale = 1.0 + Math.sin(t * 4.0 + phase) * 0.06; // pulso de +-6%
        int glowAlpha = (int) (70 + Math.sin(t * 4.0 + phase) * 40); // brillo 30..110

        AffineTransform old = g2.getTransform();
        g2.translate(x + w / 2.0, y + h / 2.0 + bob);

        g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), glowAlpha));
        g2.fillOval(-w / 2 - 6, -h / 2 - 6, w + 12, h + 12);

        g2.scale(scale, scale);
        content.accept(g2); // cada uno dibuja lo suyo, centrado en (0, 0)

        g2.setTransform(old);
    }

    // Efectos de la bala
    public void drawBulletTracer(double x, double y, int w, int h, double dirX, double dirY, double speed, int age,
            String kind) {
        bulletEffects.drawTracer(g2, x, y, w, h, dirX, dirY, speed, age, kind);
    }

    public void drawMuzzleFlash(double x, double y, int w, int h, double angle, String weaponType, long lastShoot,
            double muzzleForward, double muzzleSide) {
        bulletEffects.drawMuzzleFlash(g2, x, y, w, h, angle, weaponType, lastShoot, muzzleForward, muzzleSide);
    }

    // Camara de seguimiento
    private int camX;
    private int camY;

    public void applyCamera(int camX, int camY) {
        this.camX = camX;
        this.camY = camY;
        g2.translate(-camX, -camY);
    }

    public void resetCamera(double camX, double camY) {
        g2.translate(camX, camY);
    }
}