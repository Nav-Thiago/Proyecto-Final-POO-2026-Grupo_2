package view;

import controller.GameController;
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
    private final Map<String, Animation> playerAnimations = new HashMap<>();
    
    private BufferedImage backgroundForest;
    private BufferedImage firstLayer;

    // Mapas para asociar la clase del modelo con su recurso visual
    private final Map<String, Animation> zombieAnimations = new HashMap<>();
    private final Map<String, BufferedImage> zombieIdleSprites = new HashMap<>();
    private final Map<String, Animation> zombieAttackAnimations = new HashMap<>();

    private static final Map<String, Double> SPRITE_ROTATION = Map.of("FastZombie", Math.PI / 2, "PoisonZombie", Math.PI / 2, "TankZombie", -Math.PI / 2, "FinalBoss", -Math.PI / 2);

    private final Map<String, BufferedImage> powerUpSprites = new HashMap<>();
    private static final Map<String, Color> POWERUP_COLORS = Map.of("Heal", Color.GREEN, "Shield", Color.CYAN, "DamageBoost", Color.RED, "Ammo", Color.YELLOW, "MaxAmmo", Color.ORANGE);
    private static final Map<String, String> POWERUP_LABELS = Map.of("Heal", "HP", "Shield", "SH", "DamageBoost", "+25", "Ammo", "AMMO", "MaxAmmo", "MAX");

    private final Map<String, BufferedImage> weaponSprites = new HashMap<>();
    private final BulletEffects bulletEffects = new BulletEffects();

    private final Hud hud = new Hud();

    private Graphics2D g2;

    public GamePanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(1920, 1080));
        setFocusable(true);
        loadSprites();
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    private BufferedImage loadImage(String route) {
        try (InputStream in = getClass().getResourceAsStream(route)) {
            if (in == null) {
                System.err.println("NO SE ENCONTRO: " + route);
                return null;
            }
            BufferedImage img = ImageIO.read(in);
            if (img == null) System.err.println("FORMATO NO SOPORTADO: " + route);
            return img;
        } catch (IOException e) {
            System.err.println("Error leyendo " + route + ": " + e.getMessage());
            return null;
        }
    }

    private void loadSprites() {
        backgroundForest = loadImage("/Resources/mapa_bosque_ultimo-111026/fondo_bosque.png");
        firstLayer = loadImage("/Resources/mapa_bosque_ultimo-111026/primer_plano.png");

        // PIES DEL JUGADOR
        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];
            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/feet_run/survivor-run_" + (i + 1) + ".png";
                BufferedImage img = loadImage(route);
                if (img != null) frames[i] = img;
            }
            playerFeetAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        // MOVIMIENTO CON ARMAS
        try {
            int frameQuantity = 19;
            BufferedImage[] handgunFrames = new BufferedImage[frameQuantity];
            BufferedImage[] rifleFrames = new BufferedImage[frameQuantity];
            BufferedImage[] shotgunFrames = new BufferedImage[frameQuantity];
            BufferedImage[] knifeFrames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                handgunFrames[i] = loadImage("/Resources/imagenes/entitys/player/move_handgun/survivor-move_handgun_" + (i + 1) + ".png");
                rifleFrames[i] = loadImage("/Resources/imagenes/entitys/player/move_rifle/survivor-move_rifle_" + (i + 1) + ".png");
                shotgunFrames[i] = loadImage("/Resources/imagenes/entitys/player/move_shotgun/survivor-move_shotgun_" + (i + 1) + ".png");
                knifeFrames[i] = loadImage("/Resources/imagenes/entitys/player/move_knife/survivor-move_knife_" + (i + 1) + ".png");
            }

            playerAnimations.put("Handgun", new Animation(150, handgunFrames));
            playerAnimations.put("Rifle", new Animation(150, rifleFrames));
            playerAnimations.put("Shotgun", new Animation(150, shotgunFrames));
            playerAnimations.put("MeleeWeapon", new Animation(150, knifeFrames));

        } catch (Exception e) {
            System.err.println("Error cargando animaciones del player (move_arma)");
        }

        String base = "/Resources/imagenes/powerups/";
        powerUpSprites.put("Heal", loadImage(base + "heal.png"));
        powerUpSprites.put("Shield", loadImage(base + "shield.png"));
        powerUpSprites.put("DamageBoost", loadImage(base + "damage.png"));
        powerUpSprites.put("Ammo", loadImage(base + "ammo.png"));
        powerUpSprites.put("MaxAmmo", loadImage(base + "max_ammo.png"));

        weaponSprites.put("Handgun", loadImage("/Resources/imagenes/weapons/gun.png"));
        weaponSprites.put("Shotgun", loadImage("/Resources/imagenes/weapons/shotgun.png"));
        weaponSprites.put("Rifle", loadImage("/Resources/imagenes/weapons/rifle.png"));

        // ZOMBIES
        try {
            int frameQuantity = 16;
            BufferedImage[] frames = new BufferedImage[frameQuantity];
            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/zombie_normal/skeleton-move_" + (i + 1) + ".png";
                java.io.InputStream in = getClass().getResourceAsStream(route);
                if (in != null) {
                    frames[i] = javax.imageio.ImageIO.read(in);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            zombieAnimations.put("NormalZombie", new Animation(150, frames));
        } catch (Exception e) {
            System.err.println("Error cargando animación del zombie normal: " + e.getMessage());
        }

        try {
            java.io.InputStream inIdle = getClass().getResourceAsStream("/Resources/imagenes/entitys/zombie_forest/tank_zombie/tank_idle.png");
            if (inIdle != null) {
                zombieIdleSprites.put("TankZombie", javax.imageio.ImageIO.read(inIdle));
            }
            int tankFrameQuantity = 36;
            BufferedImage[] tankFrames = new BufferedImage[tankFrameQuantity];
            for (int i = 0; i < tankFrameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/tank_zombie/tank_walk_" + (i + 1) + ".png";
                java.io.InputStream in = getClass().getResourceAsStream(route);
                if (in != null) tankFrames[i] = javax.imageio.ImageIO.read(in);
            }
            zombieAnimations.put("TankZombie", new Animation(100, tankFrames));
        } catch (Exception e) {
            System.out.println("Error cargando animación del tanque: " + e.getMessage());
        }

        try {
            int attackFrameQuantity = 36; 
            BufferedImage[] tankAttackFrames = new BufferedImage[attackFrameQuantity];
            for (int i = 0; i < attackFrameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/tank_zombie/tank_attack_" + (i + 1) + ".png";
                java.io.InputStream in = getClass().getResourceAsStream(route);
                if (in != null) tankAttackFrames[i] = javax.imageio.ImageIO.read(in);
            }
            zombieAttackAnimations.put("TankZombie", new Animation(100, tankAttackFrames));
        } catch (Exception e) {
            System.err.println("Error cargando ataque del TankZombie: " + e.getMessage());
        }

        try {
            java.io.InputStream inIdle = getClass().getResourceAsStream("/Resources/imagenes/entitys/zombie_forest/fast_zombie/fast_idle.png");
            if (inIdle != null) {
                zombieIdleSprites.put("FastZombie", javax.imageio.ImageIO.read(inIdle));
            }
            int fastFrameQuantity = 16;
            BufferedImage[] fastFrames = new BufferedImage[fastFrameQuantity];
            for (int i = 0; i < fastFrameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/fast_zombie/fast_walk_" + (i + 1) + ".png";
                java.io.InputStream in = getClass().getResourceAsStream(route);
                if (in != null) fastFrames[i] = javax.imageio.ImageIO.read(in);
            }
            zombieAnimations.put("FastZombie", new Animation(80, fastFrames));
        } catch (Exception e) {
            System.out.println("Error cargando animación del zombie rápido: " + e.getMessage());
        }

        try {
            int attackFrameQuantity = 36; 
            BufferedImage[] fastAttackFrames = new BufferedImage[attackFrameQuantity];
            for (int i = 0; i < attackFrameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/fast_zombie/fast_attack_" + (i + 1) + ".png";
                java.io.InputStream in = getClass().getResourceAsStream(route);
                if (in != null) fastAttackFrames[i] = javax.imageio.ImageIO.read(in);
            }
            zombieAttackAnimations.put("FastZombie", new Animation(70, fastAttackFrames));
        } catch (Exception e) {
            System.err.println("Error cargando ataque del FastZombie: " + e.getMessage());
        }

        try {
            java.io.InputStream inIdle = getClass().getResourceAsStream("/Resources/imagenes/entitys/zombie_forest/poison_zombie/poison_idle.png");
            if (inIdle != null) {
                zombieIdleSprites.put("PoisonZombie", javax.imageio.ImageIO.read(inIdle));
            }

            int poisonFrameQuantity = 16;
            BufferedImage[] poisonFrames = new BufferedImage[poisonFrameQuantity];
            for (int i = 0; i < poisonFrameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/poison_zombie/poison_walk_" + (i + 1) + ".png";
                java.io.InputStream in = getClass().getResourceAsStream(route);
                if (in != null) poisonFrames[i] = javax.imageio.ImageIO.read(in);
            }
            zombieAnimations.put("PoisonZombie", new Animation(150, poisonFrames));
        } catch (Exception e) {
            System.out.println("Error cargando animación del zombie venenoso: " + e.getMessage());
        }

        try {
            int attackFrameQuantity = 36; 
            BufferedImage[] poisonAttackFrames = new BufferedImage[attackFrameQuantity];
            for (int i = 0; i < attackFrameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/poison_zombie/poison_attack_" + (i + 1) + ".png";
                java.io.InputStream in = getClass().getResourceAsStream(route);
                if (in != null) poisonAttackFrames[i] = javax.imageio.ImageIO.read(in);
            }
            zombieAttackAnimations.put("PoisonZombie", new Animation(100, poisonAttackFrames));
        } catch (Exception e) {
            System.err.println("Error cargando ataque del PoisonZombie: " + e.getMessage());
        }
        
        try {
            InputStream inIdle = getClass().getResourceAsStream("/Resources/imagenes/entitys/zombie_forest/boss_zombie/boss_idle.png");
            if (inIdle != null) zombieIdleSprites.put("FinalBoss", ImageIO.read(inIdle));

            int walkFramesCount = 36; 
            BufferedImage[] walkFrames = new BufferedImage[walkFramesCount];
            for(int i = 0; i < walkFramesCount; i++){
                 String route = "/Resources/imagenes/entitys/zombie_forest/boss_zombie/boss_walk_" + (i+1) + ".png";
                 InputStream in = getClass().getResourceAsStream(route);
                 if (in != null) walkFrames[i] = ImageIO.read(in);
            }
            zombieAnimations.put("FinalBoss", new Animation(150, walkFrames));

            int attackFramesCount = 36; 
            BufferedImage[] attackFrames = new BufferedImage[attackFramesCount];
            for(int i = 0; i < attackFramesCount; i++){
                 String route = "/Resources/imagenes/entitys/zombie_forest/boss_zombie/boss_attack_" + (i+1) + ".png";
                 InputStream in = getClass().getResourceAsStream(route);
                 if (in != null) attackFrames[i] = ImageIO.read(in);
            }
            zombieAttackAnimations.put("FinalBoss", new Animation(100, attackFrames));

        } catch (Exception e) {
             System.err.println("Error cargando animaciones del FinalBoss: " + e.getMessage());
        }
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
    public void drawZombie(double x, double y, int w, int h, double angle, int health, String type, boolean isAttacking) {
        double spriteAngle = angle + SPRITE_ROTATION.getOrDefault(type, 0.0);

        AffineTransform transformOriginal = g2.getTransform();
        double centerX = x + (w / 2.0);
        double centerY = y + (h / 2.0);

        g2.translate(centerX, centerY);
        g2.rotate(spriteAngle);

        Animation attackAnim = zombieAttackAnimations.get(type);
        Animation walkAnim = zombieAnimations.get(type);
        BufferedImage idle = zombieIdleSprites.get(type);

        BufferedImage frameToDraw = null;

        // Lógica de prioridad: Ataque -> Caminar -> Reposo -> Círculo rojo de error
        if (isAttacking && attackAnim != null && attackAnim.getCurrentFrame() != null) {
            frameToDraw = attackAnim.getCurrentFrame();
        } else if (walkAnim != null && walkAnim.getCurrentFrame() != null) {
            frameToDraw = walkAnim.getCurrentFrame();
        } else if (idle != null) {
            frameToDraw = idle;
        }

        if (frameToDraw != null) {
            g2.drawImage(frameToDraw, -w / 2, -h / 2, w, h, null);
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

    private void drawMatchSummary(Graphics2D g2, long matchTimeMs, long bestTimeMs, boolean newRecord) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        int y = getHeight() / 2 + 50;

        g2.setColor(Color.WHITE);
        drawCenterText(g2, "Tu tiempo: " + formatTime(matchTimeMs), y);
        drawCenterText(g2, "Mejor tiempo: " + formatTime(bestTimeMs), y + 35);

        if (newRecord) {
            g2.setColor(Color.YELLOW);
            drawCenterText(g2, "¡NUEVO RÉCORD!", y + 80);
        }
    }

    // Mensajes de estado
    public void drawGameOver(long matchTimeMs, long bestTimeMS, boolean newRecord) {
        showFinalMessage(g2,"JUEGO TERMINADO", Color.RED);
        drawMatchSummary(g2, matchTimeMs, bestTimeMS, newRecord);
    }

    public void drawGameWon(long matchTimeMs, long bestTimeMS, boolean newRecord) {
        showFinalMessage(g2,"¡GANASTE!", Color.GREEN);
        drawMatchSummary(g2, matchTimeMs, bestTimeMS, newRecord);
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