package view;

import controller.GameController;
import model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

import java.awt.geom.Area;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class GamePanel extends JPanel {

    private final GameController controller;

    private Animation playerWalkAnimation;
    private Animation playersFeetAnimation;

    private BufferedImage gunSprite;
    private BufferedImage shotGunSprite;

    private BufferedImage backgroundForest;
    private BufferedImage firstLayer;

    // Mapas para asociar la clase del modelo con su recurso visual
    private final Map<Class<? extends Enemy>, Animation> zombieAnimations = new HashMap<>();
    private final Map<Class<? extends Enemy>, BufferedImage> zombieIdleSprites = new HashMap<>();
    private final Map<Class<? extends Enemy>, Animation> zombieAttackAnimations = new HashMap<>(); 
    private final Map<Class<? extends PowerUp>, BufferedImage> powerUpSprites = new HashMap<>();
    private final Map<Class<? extends FireArm>, BufferedImage> weaponSprites = new HashMap<>();

    public GamePanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(1920, 1080));
        setFocusable(true);
        loadSprites("bosque");
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

    public void loadSprites(String entorno) {
        
        // --- MAPAS ---
        try {
            InputStream inBackground = getClass().getResourceAsStream("/Resources/mapa_" + entorno + "/fondo_" + entorno + ".png");
            if (inBackground != null) backgroundForest = ImageIO.read(inBackground);
            
            InputStream inFirstLayer = getClass().getResourceAsStream("/Resources/mapa_" + entorno + "/primer_plano.png");
            if (inFirstLayer != null) firstLayer = ImageIO.read(inFirstLayer);
        } catch (Exception e) {
            System.err.println("Error cargando mapas: " + e.getMessage());
        }

        // --- JUGADOR ---
        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];
            BufferedImage[] feetFrames = new BufferedImage[frameQuantity];
            
            for (int i = 0; i < frameQuantity; i++) {
                String routeHandgun = "/Resources/imagenes/entitys/player/survivor-move_handgun_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(routeHandgun);
                if (inHandgun != null) frames[i] = ImageIO.read(inHandgun);

                String routeFeet = "/Resources/imagenes/entitys/player/survivor-run_" + (i+1) + ".png";
                InputStream inFeet = getClass().getResourceAsStream(routeFeet);
                if (inFeet != null) feetFrames[i] = ImageIO.read(inFeet);
            }
            playerWalkAnimation = new Animation(150, frames);
            playersFeetAnimation = new Animation(150, feetFrames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        // --- ARMAS ---
        try {
            InputStream inGun = getClass().getResourceAsStream("/Resources/imagenes/weapons/gun.png");
            if (inGun != null) {
                gunSprite = ImageIO.read(inGun);
                weaponSprites.put(FireArm.class, gunSprite);
            }
            InputStream inShotgun = getClass().getResourceAsStream("/Resources/imagenes/weapons/shotgun.png");
            if (inShotgun != null) {
                shotGunSprite = ImageIO.read(inShotgun);
                weaponSprites.put(ShotGun.class, shotGunSprite);
            }
        } catch (IOException e) {
            System.err.println("Error cargando sprites de armas: " + e.getMessage());
        }

        // --- POWER UPS ---
        String base = "/Resources/imagenes/powerups/";
        powerUpSprites.put(Heal.class, loadImage(base + "heal.png"));
        powerUpSprites.put(Shield.class, loadImage(base + "shield.png"));
        powerUpSprites.put(DamageBoost.class, loadImage(base + "damage.png"));
        powerUpSprites.put(Ammo.class, loadImage(base + "ammo.png"));
        powerUpSprites.put(MaxAmmo.class, loadImage(base + "max_ammo.png"));

        // --- ZOMBIES ---
        String carpetaZombies = entorno.equals("bosque") ? "zombie_forest" : "zombie_city";
        zombieAnimations.clear();
        zombieIdleSprites.clear();
        zombieAttackAnimations.clear();

        // ZOMBIE NORMAL
        try {
            String rutaNormal = "/Resources/imagenes/entitys/" + carpetaZombies + "/zombie_normal/";
            InputStream inIdle = getClass().getResourceAsStream(rutaNormal + "zombie_idle.png");
            if (inIdle != null) zombieIdleSprites.put(NormalZombie.class, ImageIO.read(inIdle));

            int framesCaminar = 16;
            BufferedImage[] walkFrames = new BufferedImage[framesCaminar];
            for (int i = 0; i < framesCaminar; i++) {
                InputStream in = getClass().getResourceAsStream(rutaNormal + "skeleton-move_" + (i+1) + ".png");
                if (in != null) walkFrames[i] = ImageIO.read(in);
            }
            zombieAnimations.put(NormalZombie.class, new Animation(150, walkFrames));

            int framesAtaque = 10; 
            BufferedImage[] attackFrames = new BufferedImage[framesAtaque];
            for (int i = 0; i < framesAtaque; i++) {
                InputStream in = getClass().getResourceAsStream(rutaNormal + "attack_" + (i+1) + ".png");
                if (in != null) attackFrames[i] = ImageIO.read(in);
            }
            zombieAttackAnimations.put(NormalZombie.class, new Animation(100, attackFrames));
        } catch (Exception e) {
            System.err.println("Error Zombie Normal: " + e.getMessage());
        }

        // TANK ZOMBIE
        try {
            String rutaTank = "/Resources/imagenes/entitys/" + carpetaZombies + "/tank_zombie/";
            InputStream inIdle = getClass().getResourceAsStream(rutaTank + "tank_idle.png");
            if (inIdle != null) zombieIdleSprites.put(TankZombie.class, ImageIO.read(inIdle));

            int framesCaminar = 36;
            BufferedImage[] walkFrames = new BufferedImage[framesCaminar];
            for (int i = 0; i < framesCaminar; i++) {
                InputStream in = getClass().getResourceAsStream(rutaTank + "tank_walk_" + (i+1) + ".png");
                if (in != null) walkFrames[i] = ImageIO.read(in);
            }
            zombieAnimations.put(TankZombie.class, new Animation(100, walkFrames));

            int framesAtaque = 12; 
            BufferedImage[] attackFrames = new BufferedImage[framesAtaque];
            for (int i = 0; i < framesAtaque; i++) {
                InputStream in = getClass().getResourceAsStream(rutaTank + "attack_" + (i+1) + ".png");
                if (in != null) attackFrames[i] = ImageIO.read(in);
            }
            zombieAttackAnimations.put(TankZombie.class, new Animation(100, attackFrames));
        } catch (Exception e) {
            System.err.println("Error Tank Zombie: " + e.getMessage());
        }

        // FAST ZOMBIE
        try {
            String rutaFast = "/Resources/imagenes/entitys/" + carpetaZombies + "/fast_zombie/";
            InputStream inIdle = getClass().getResourceAsStream(rutaFast + "fast_idle.png");
            if (inIdle != null) zombieIdleSprites.put(FastZombie.class, ImageIO.read(inIdle));

            int framesCaminar = 16;
            BufferedImage[] walkFrames = new BufferedImage[framesCaminar];
            for (int i = 0; i < framesCaminar; i++) {
                InputStream in = getClass().getResourceAsStream(rutaFast + "fast_walk_" + (i+1) + ".png");
                if (in != null) walkFrames[i] = ImageIO.read(in);
            }
            zombieAnimations.put(FastZombie.class, new Animation(80, walkFrames));

            int framesAtaque = 8; 
            BufferedImage[] attackFrames = new BufferedImage[framesAtaque];
            for (int i = 0; i < framesAtaque; i++) {
                InputStream in = getClass().getResourceAsStream(rutaFast + "attack_" + (i+1) + ".png");
                if (in != null) attackFrames[i] = ImageIO.read(in);
            }
            zombieAttackAnimations.put(FastZombie.class, new Animation(70, attackFrames));
        } catch (Exception e) {
            System.err.println("Error Fast Zombie: " + e.getMessage());
        }

        // POISON ZOMBIE
        try {
            String rutaPoison = "/Resources/imagenes/entitys/" + carpetaZombies + "/poison_zombie/";
            InputStream inIdle = getClass().getResourceAsStream(rutaPoison + "poison_idle.png");
            if (inIdle != null) zombieIdleSprites.put(PoisonZombie.class, ImageIO.read(inIdle));

            int framesCaminar = 16;
            BufferedImage[] walkFrames = new BufferedImage[framesCaminar];
            for (int i = 0; i < framesCaminar; i++) {
                InputStream in = getClass().getResourceAsStream(rutaPoison + "poison_walk_" + (i+1) + ".png");
                if (in != null) walkFrames[i] = ImageIO.read(in);
            }
            zombieAnimations.put(PoisonZombie.class, new Animation(150, walkFrames));

            int framesAtaque = 14; 
            BufferedImage[] attackFrames = new BufferedImage[framesAtaque];
            for (int i = 0; i < framesAtaque; i++) {
                InputStream in = getClass().getResourceAsStream(rutaPoison + "attack_" + (i+1) + ".png");
                if (in != null) attackFrames[i] = ImageIO.read(in);
            }
            zombieAttackAnimations.put(PoisonZombie.class, new Animation(120, attackFrames));
        } catch (Exception e) {
            System.err.println("Error Poison Zombie: " + e.getMessage());
        }

        // FINAL BOSS
        try {
            String rutaBoss = "/Resources/imagenes/entitys/" + carpetaZombies + "/boss_zombie/";
            InputStream inIdle = getClass().getResourceAsStream(rutaBoss + "boss_idle.png");
            if (inIdle != null) zombieIdleSprites.put(FinalBoss.class, ImageIO.read(inIdle));

            int framesCaminar = 36; 
            BufferedImage[] walkFrames = new BufferedImage[framesCaminar];
            for (int i = 0; i < framesCaminar; i++) {
                InputStream in = getClass().getResourceAsStream(rutaBoss + "boss_walk_" + (i+1) + ".png");
                if (in != null) walkFrames[i] = ImageIO.read(in);
            }
            zombieAnimations.put(FinalBoss.class, new Animation(150, walkFrames));

            int framesAtaque = 36; 
            BufferedImage[] attackFrames = new BufferedImage[framesAtaque];
            for (int i = 0; i < framesAtaque; i++) {
                InputStream in = getClass().getResourceAsStream(rutaBoss + "boss_attack_" + (i+1) + ".png");
                if (in != null) attackFrames[i] = ImageIO.read(in);
            }
            zombieAttackAnimations.put(FinalBoss.class, new Animation(100, attackFrames));
            
        } catch (Exception e) {
            System.err.println("Error cargando Final Boss: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Player player = this.controller.getState().getPlayer();

        int screenWidth = getWidth();
        int screenHeight = getHeight();
        int mapWidth = this.controller.getState().getMapWidth();
        int mapHeight = this.controller.getState().getMapHeight();

        int camX = (int) player.getX() - (screenWidth / 2) + (player.getWidth() / 2);
        int camY = (int) player.getY() - (screenHeight / 2) + (player.getHeight() / 2);
        camX = Math.max(0, Math.min(camX, mapWidth - screenWidth));
        camY = Math.max(0, Math.min(camY, mapHeight - screenHeight));

        g2.translate(-camX, -camY);

        if (backgroundForest != null) {
            g2.drawImage(backgroundForest, 0, 0, mapWidth, mapHeight, null);
        }

        drawPowerUps(g2); 
        drawGroundWeapons(g2); 

        for(Animation anim: zombieAnimations.values()){
            anim.update();
        }
        for(Animation anim: zombieAttackAnimations.values()){
            anim.update();
        }

        for (Enemy zombie : this.controller.getState().getEnemies()) {
            if (zombie.isAlive()) {
                AffineTransform transformOriginal = g2.getTransform();
                double centerX = zombie.getX() + (zombie.getWidth() / 2.0);
                double centerY = zombie.getY() + (zombie.getHeight() / 2.0);

                g2.translate(centerX, centerY);
                g2.rotate(zombie.getAngle());

                int w = zombie.getWidth();
                int h = zombie.getHeight();

                Animation anim = zombieAnimations.get(zombie.getClass());
                BufferedImage idle = zombieIdleSprites.get(zombie.getClass());

                if (anim != null && anim.getCurrentFrame() != null) {
                    g2.drawImage(anim.getCurrentFrame(), -w / 2, -h / 2, w, h, null);
                } else if (idle != null) {
                    g2.drawImage(idle, -w / 2, -h / 2, w, h, null);
                } else {
                    g2.setColor(Color.RED);
                    g2.fillOval(-w / 2, -h / 2, w, h);
                }

                g2.setTransform(transformOriginal);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.BOLD, 20));
                String lifeText = "HP: " + zombie.getHealth();
                g2.drawString(lifeText, (int) zombie.getX(), (int) zombie.getY());
            }
        }

        g2.setColor(Color.ORANGE);
        for (Bullet bullet : this.controller.getState().getPlayerBullets()) {
            g2.fillOval((int) bullet.getX(), (int) bullet.getY(), bullet.getWidth() - 2, bullet.getHeight() - 2);
        }

        g2.setColor(new Color(50, 205, 50));
        for (Bullet poison : this.controller.getState().getPoisonBullets()) {
            g2.fillOval((int) poison.getX(), (int) poison.getY(), poison.getWidth(), poison.getHeight());
        }

        if (playerWalkAnimation != null) playerWalkAnimation.update();
        if (playersFeetAnimation != null) playersFeetAnimation.update();
        
        if (player.isAlive()) {
            AffineTransform transformOriginal = g2.getTransform();
            double centerX = player.getX() + (player.getWidth() / 2.0);
            double centerY = player.getY() + (player.getHeight() / 2.0);

            g2.translate(centerX, centerY);
            g2.rotate(player.getAngle());
            int w = player.getWidth();
            int h = player.getHeight();

            if (player.isMoving() && playersFeetAnimation != null && playersFeetAnimation.getCurrentFrame() != null) {
                AffineTransform transformFeet = g2.getTransform();
                g2.scale(0.6, 0.6);
                g2.drawImage(playersFeetAnimation.getCurrentFrame(), -w / 2, -h / 2, w, h, null);
                g2.setTransform(transformFeet);
            }

            if (playerWalkAnimation != null && playerWalkAnimation.getCurrentFrame() != null) {
                g2.drawImage(playerWalkAnimation.getCurrentFrame(), -w / 2, -h / 2, w, h, null);
            } else {
                g2.setColor(Color.BLUE);
                g2.fillOval(-w / 2, -h / 2, w, h);
            }
            g2.setTransform(transformOriginal);
        }

        if (firstLayer != null) {
            g2.drawImage(firstLayer, 0, 0, mapWidth, mapHeight, null);
        }

        g2.setStroke(new BasicStroke(2.0f)); 
        g2.setColor(Color.GREEN);
        Rectangle pBounds = player.getBounds();
        g2.drawRect(pBounds.x, pBounds.y, pBounds.width, pBounds.height);

        g2.setColor(Color.RED);
        for (Enemy zombie : this.controller.getState().getEnemies()) {
            if (zombie.isAlive()) {
                Rectangle zBounds = zombie.getBounds();
                g2.drawRect(zBounds.x, zBounds.y, zBounds.width, zBounds.height);
            }
        }

        g2.setColor(Color.CYAN);
        for (Bullet bullet : this.controller.getState().getPlayerBullets()) {
            if (bullet.isActive()) {
                Rectangle bBounds = bullet.getBounds();
                g2.drawRect(bBounds.x, bBounds.y, bBounds.width, bBounds.height);
            }
        }

        double pCenterX = player.getX() + (player.getWidth() / 2);
        double pCenterY = player.getY() + (player.getHeight() / 2);

        Area darkness = new Area(new Rectangle2D.Double(0, 0, mapWidth, mapHeight));
        double lightRadius = 700.0; 
        double fovAngle = 85.0; 
        double angleDegrees = -Math.toDegrees(player.getAngle());

        Arc2D.Double lightCone = new Arc2D.Double(
                pCenterX - lightRadius, pCenterY - lightRadius,
                lightRadius * 2, lightRadius * 2,
                angleDegrees - (fovAngle / 2.0), fovAngle,
                Arc2D.PIE
        );

        double personalRadius = 90.0;
        Ellipse2D.Double personalCircle = new Ellipse2D.Double(
                pCenterX - personalRadius, pCenterY - personalRadius,
                personalRadius * 2, personalRadius * 2
        );

        darkness.subtract(new Area(lightCone)); 
        darkness.subtract(new Area(personalCircle)); 

        g2.setColor(new Color(0, 0, 0, 220)); 
        g2.fill(darkness);

        g2.setStroke(new BasicStroke(1.0f)); 
        g2.translate(camX, camY);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20)); 
        g2.drawString("Supervivencia: " + player.getHealth() + " / " + player.getMaxHealth(), 20, 30); 

        if (player.getShield() > 0) {
            g2.setColor(Color.CYAN);
            g2.drawString("Escudo: " + player.getShield(), 20, 55);
        }
        if (player.getDamageBonus() > 0) {
            g2.setColor(Color.RED);
            g2.drawString("Daño +" + Math.round(player.getDamageBonus()), 20, 80);
        }

        Weapon activeWeapon = player.getActiveWeapon();
        if (activeWeapon instanceof FireArm fa) {
            String ammoText = "Arma: " + fa.getClass().getSimpleName() + " - Balas: " + fa.getActualMunition() + " / " + fa.getMaxMunition();
            g2.setColor(Color.WHITE);
            g2.drawString(ammoText, 20, 105);
        } else if (activeWeapon instanceof MeleeWeapon) {
            g2.setColor(Color.WHITE);
            g2.drawString("Arma: Cuchillo", 20, 105);
        }

        GameModel.gameStatus status = controller.getStatus();

        if (status == GameModel.gameStatus.GAME_OVER) {
            showFinalMessage(g2, "JUEGO TERMINADO", Color.RED);
        } else if (status == GameModel.gameStatus.LEVEL_COMPLETED) {
            showFinalMessage(g2, "¡FELICITACIONES, HAS COMPLETADO EL NIVEL CON ÉXITO!", Color.YELLOW);
        } else if (status == GameModel.gameStatus.GAME_WON) {
            showFinalMessage(g2, "¡GANASTE!", Color.GREEN);
        } else if (status == GameModel.gameStatus.PAUSED) {
            showFinalMessage(g2, "PAUSA (ESC para continuar)", Color.WHITE);
        } else if (status == GameModel.gameStatus.BOSS_INTRO) {
            drawBossIntro(g2);
            showFinalMessage(g2, "PAUSA (ESC para continuar)", Color.WHITE);
        }
    }

    private void drawGroundWeapons(Graphics2D g2) {
        for (WeaponItem item : this.controller.getState().getGroundWeapons()) {
            BufferedImage sprite = weaponSprites.get(item.getWeapon().getClass());
            int x = (int) item.getX();
            int y = (int) item.getY();
            int w = item.getWidth();
            int h = item.getHeight();

            if (sprite != null) {
                g2.drawImage(sprite, x, y, w, h, null);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(x, y, w, h);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g2.drawString(item.getWeapon().getClass().getSimpleName(), x, y - 5);
            }
        }
    }

    private void drawPowerUps(Graphics2D g2) {
        double t = System.currentTimeMillis() / 1000.0;
        for (PowerUp p : this.controller.getState().getPowerUps()) {
            int w = p.getWidth();
            int h = p.getHeight();
            double phase = p.getX() * 0.05;
            double bob = Math.sin(t * 3.0 + phase) * 4;              
            double scale = 1.0 + Math.sin(t * 4.0 + phase) * 0.06;   
            int glowAlpha = (int) (70 + Math.sin(t * 4.0 + phase) * 40); 

            BufferedImage pSprite = powerUpSprites.get(p.getClass());
            AffineTransform old = g2.getTransform();
            g2.translate(p.getX() + w / 2.0, p.getY() + h / 2.0 + bob);

            Color c = p.getColor();
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), glowAlpha));
            g2.fillOval(-w / 2 - 6, -h / 2 - 6, w + 12, h + 12);
            g2.scale(scale, scale);

            if (pSprite != null) {
                g2.drawImage(pSprite, -w / 2, -h / 2, w, h, null);
            } else {
                g2.setColor(c);
                g2.fillOval(-w / 2, -h / 2, w, h);
                g2.setColor(Color.BLACK);
                g2.drawOval(-w / 2, -h / 2, w, h);
                g2.setFont(new Font("Monospaced", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String label = p.getLabel();
                g2.drawString(label, -fm.stringWidth(label) / 2, (fm.getAscent() / 2) - 2);
            }
            g2.setTransform(old);
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

    private void drawBossIntro(Graphics2D g2) {
        int step = controller.getBossIntroStep();
        double p = controller.getBossIntroProgress();

        double fade = Math.max(0.0, Math.min(1.0, Math.min(p / 0.25, (1.0 - p) / 0.25)));
        int alpha = (int) (255 * fade);

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

        if (step == 1) {
            x += (int) (Math.random() * 5) - 2;
            y += (int) (Math.random() * 5) - 2;
        }

        g2.setColor(new Color(70, 0, 0, alpha));
        g2.drawString(text, x + 3, y + 3);
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g2.drawString(text, x, y);
    }
}