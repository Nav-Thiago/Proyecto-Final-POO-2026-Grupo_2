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
import java.util.function.Consumer;
import java.util.HashMap;

import java.awt.geom.Area;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class GamePanel extends JPanel {

    private final GameController controller;

    private Animation playersFeetAnimation;

    private Animation playerKnifeWalkAnimation;
    private Animation playerKnifeAttackAnimation;

    private Animation playerHandGunWalkAnimation;
    private Animation playerHandGunAttackAnimation;

    private Animation playerRifleWalkAnimation;
    private Animation playerRifleAttackAnimation;

    private Animation playerShotgunWalkAnimation;
    private Animation playerShotgunAttackAnimation;

    private Animation zombieWalkAnimation;

    private BufferedImage tankIdleSprite;
    private Animation tankWalkAnimation;

    private BufferedImage fastIdleSprite;
    private Animation fastWalkAnimation;

    private BufferedImage poisonIdleSprite;
    private Animation poisonWalkAnimation;

    private BufferedImage backgroundForest;
    private BufferedImage firstLayer;

    // Mapas para asociar la clase del modelo con su recurso visual
    private final Map<Class<? extends Enemy>, Animation> zombieAnimations = new HashMap<>();
    private final Map<Class<? extends Enemy>, BufferedImage> zombieIdleSprites = new HashMap<>();

    private final Map<Class<? extends PowerUp>, BufferedImage> powerUpSprites = new HashMap<>();

    private final Map<Class<? extends FireArm>, BufferedImage> weaponSprites = new HashMap<>();

    public GamePanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(1920, 1080));
        setFocusable(true);
        loadSprites();
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
        try {
            InputStream inBackground = getClass().getResourceAsStream("/Resources/mapa_bosque/fondo_bosque.png");
            if (inBackground != null) {
                backgroundForest = ImageIO.read(inBackground);
            } else {
                System.out.println("No se encontró el png del mapa");
            }
            InputStream inFirstLayer = getClass().getResourceAsStream("/Resources/mapa_bosque/primer_plano.png");
            if (inFirstLayer != null) firstLayer = ImageIO.read(inFirstLayer);
        } catch (Exception e) {
            System.err.println("Error cargando mapas: " + e.getMessage());
        }

        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor_feet_run/survivor-run_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(route);

                if (inHandgun != null) {
                    frames[i] = ImageIO.read(inHandgun);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            playersFeetAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor_move_knife/survivor-move_knife_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(route);

                if (inHandgun != null) {
                    frames[i] = ImageIO.read(inHandgun);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            playerKnifeWalkAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor_move_handgun/survivor-move_handgun_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(route);

                if (inHandgun != null) {
                    frames[i] = ImageIO.read(inHandgun);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            playerHandGunWalkAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor_move_rifle/survivor-move_rifle_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(route);

                if (inHandgun != null) {
                    frames[i] = ImageIO.read(inHandgun);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            playerRifleWalkAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor_move_shotgun/survivor-move_shotgun_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(route);

                if (inHandgun != null) {
                    frames[i] = ImageIO.read(inHandgun);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            playerShotgunWalkAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        try {
            int frameQuantity = 3;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor_shoot_handgun/survivor-shoot_handgun_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(route);

                if (inHandgun != null) {
                    frames[i] = ImageIO.read(inHandgun);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            playerHandGunAttackAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor_shoot_rifle/survivor-shoot_rifle_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(route);

                if (inHandgun != null) {
                    frames[i] = ImageIO.read(inHandgun);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            playerRifleAttackAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor_shoot_shotgun/survivor-shoot_shotgun_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(route);

                if (inHandgun != null) {
                    frames[i] = ImageIO.read(inHandgun);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            playerShotgunAttackAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        try {
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor_meleeattack_knife/survivor-meleeattack_knife_" + (i + 1) + ".png";
                InputStream inHandgun = getClass().getResourceAsStream(route);

                if (inHandgun != null) {
                    frames[i] = ImageIO.read(inHandgun);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }
            playerKnifeAttackAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del player: " + e.getMessage());
        }

        String base = "/Resources/imagenes/powerups/";
        powerUpSprites.put(Heal.class, loadImage(base + "heal.png"));
        powerUpSprites.put(Shield.class, loadImage(base + "shield.png"));
        powerUpSprites.put(DamageBoost.class, loadImage(base + "damage.png"));
        powerUpSprites.put(Ammo.class, loadImage(base + "ammo.png"));
        powerUpSprites.put(MaxAmmo.class, loadImage(base + "max_ammo.png"));

        weaponSprites.put(FireArm.class, loadImage("/Resources/imagenes/weapons/gun.png"));
        weaponSprites.put(ShotGun.class, loadImage("/Resources/imagenes/weapons/shotgun.png"));

        try {
            int frameQuantity = 16;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/zombie_normal/skeleton-move_" + (i + 1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);

                if (in != null) {
                    frames[i] = ImageIO.read(in);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }

            zombieWalkAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del zombie: " + e.getMessage());
        }

        try {
            InputStream in = getClass().getResourceAsStream("/Resources/imagenes/entitys/zombie_forest/tank_zombie/tank_idle.png");
            if (in != null) {
                tankIdleSprite = ImageIO.read(in);
            }
        } catch (IOException e) {
            System.out.println("Error cargando sprite del tanque en reposo: " + e.getMessage());
        }

        try {
            int tankFrameQuantity = 36;
            BufferedImage[] tankFrames = new BufferedImage[tankFrameQuantity];

            for (int i = 0; i < tankFrameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/tank_zombie/tank_walk_" + (i + 1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);

                if (in != null) {
                    tankFrames[i] = ImageIO.read(in);
                } else {
                    System.out.println("Advertencia: No se encontro la imagen " + route);
                }
            }
            tankWalkAnimation = new Animation(100, tankFrames);

        } catch (Exception e) {
            System.out.println("Error cargando animación del tanque: " + e.getMessage());
        }

        try {
            InputStream in = getClass().getResourceAsStream("/Resources/imagenes/entitys/zombie_forest/fast_zombie/fast_idle.png");
            if (in != null) fastIdleSprite = ImageIO.read(in);
        } catch (IOException e) {
            System.out.println("Error cargando sprite del fast zombie en reposo: " + e.getMessage());
        }

        try {
            int fastFrameQuantity = 16;
            BufferedImage[] fastFrames = new BufferedImage[fastFrameQuantity];
            for (int i = 0; i < fastFrameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/fast_zombie/fast_walk_" + (i + 1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);
                if (in != null) fastFrames[i] = ImageIO.read(in);
            }
            fastWalkAnimation = new Animation(80, fastFrames);
        } catch (Exception e) {
            System.out.println("Error cargando animación del zombie rápido: " + e.getMessage());
        }

        try {
            InputStream in = getClass().getResourceAsStream("/Resources/imagenes/entitys/zombie_forest/poison_zombie/poison_idle_png");
            if (in != null) poisonIdleSprite = ImageIO.read(in);
        } catch (IOException e) {
            System.out.println("Error cargando sprite del poison zombie en reposo: " + e.getMessage());
        }

        try {
            int poisonFrameQuantity = 16;
            BufferedImage[] poisonFrames = new BufferedImage[poisonFrameQuantity];
            for (int i = 0; i < poisonFrameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_forest/poison_zombie/poison_walk_" + (i + 1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);
                if (in != null) poisonFrames[i] = ImageIO.read(in);
            }
            poisonWalkAnimation = new Animation(150, poisonFrames);
        } catch (Exception e) {
            System.out.println("Error cargando animación del zombie venenoso: " + e.getMessage());
        }

        if (zombieWalkAnimation != null) {
            zombieAnimations.put(NormalZombie.class, zombieWalkAnimation);
        }
        if (tankWalkAnimation != null) {
            zombieAnimations.put(TankZombie.class, tankWalkAnimation);
        }
        if (tankIdleSprite != null) {
            zombieIdleSprites.put(TankZombie.class, tankIdleSprite);
        }
        if (fastWalkAnimation != null) {
            zombieAnimations.put(FastZombie.class, fastWalkAnimation);
        }
        if (fastIdleSprite != null) {
            zombieIdleSprites.put(FastZombie.class, fastIdleSprite);
        }
        if (poisonWalkAnimation != null) {
            zombieAnimations.put(PoisonZombie.class, poisonWalkAnimation);
        }
        if (poisonIdleSprite != null) {
            zombieAnimations.put(PoisonZombie.class, poisonWalkAnimation);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Player player = this.controller.getState().getPlayer();

        ///////////////////////////////////////
        // SEGUIMIENTO DE CAMARA A PERSONAJE //
        ///////////////////////////////////////

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        int mapWidth = this.controller.getState().getMapWidth();
        int mapHeight = this.controller.getState().getMapHeight();

        // Calculo de camara
        int camX = (int) player.getX() - (screenWidth / 2) + (player.getWidth() / 2);
        int camY = (int) player.getY() - (screenHeight / 2) + (player.getHeight() / 2);

        // Calculo para no irse del mapa
        camX = Math.max(0, Math.min(camX, mapWidth - screenWidth));
        camY = Math.max(0, Math.min(camY, mapHeight - screenHeight));

        g2.translate(-camX, -camY);

        if (backgroundForest != null) {
            g2.drawImage(backgroundForest, 0, 0, mapWidth, mapHeight, null);
        }

        // Dibuja PowerUps
        drawPowerUps(g2); // [POWER-UP] primero, para que queden debajo de zombies y jugador

        // Dibuja Armas en el suelo
        drawGroundWeapons(g2); // [ARMAS] segundo, para que queden debajo de zombies y jugador

        // Dibuja a los enemigos
        if (zombieWalkAnimation != null) {
            zombieWalkAnimation.update();
        }

        //Dibuja Zombie Tanque
        if (tankWalkAnimation != null) {
            tankWalkAnimation.update();
        }

        for (Animation anim : zombieAnimations.values()) {
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

                int textX = (int) zombie.getX();
                int textY = (int) zombie.getY();
                g2.drawString(lifeText, textX, textY);
            }
        }
        ///////////////////////////////////////
        //   Dibuja las balas del jugador    //
        ///////////////////////////////////////

        g2.setColor(Color.ORANGE);
        for (Bullet bullet : this.controller.getState().getPlayerBullets()) {
            g2.fillOval((int) bullet.getX(), (int) bullet.getY(), bullet.getWidth() - 2, bullet.getHeight() - 2);
        }

        ///////////////////////////////////////
        //   Dibuja las balas venenosas      //
        ///////////////////////////////////////

        g2.setColor(new Color(50, 205, 50));
        for (Bullet poison : this.controller.getState().getPoisonBullets()) {
            g2.fillOval((int) poison.getX(), (int) poison.getY(), poison.getWidth(), poison.getHeight());
        }

        ///////////////////////////////////////
        //          Dibuja al jugador        //
        ///////////////////////////////////////

        if (playersFeetAnimation != null) {
            playersFeetAnimation.update();
        }

        if (playerKnifeWalkAnimation != null) {
            playerKnifeWalkAnimation.update();
        }

        if (controller.getIsAlive()) {
            AffineTransform transformOriginal = g2.getTransform();

            double centerX = controller.getPlayerX() + (player.getWidth() / 2.0);
            double centerY = player.getY() + (player.getHeight() / 2.0);

            g2.translate(centerX, centerY);
            g2.rotate(player.getAngle());

            int w = player.getWidth();
            int h = player.getHeight();

            if (player.isMoving() && playersFeetAnimation != null && playersFeetAnimation.getCurrentFrame() != null) {
                BufferedImage feetFrame = playersFeetAnimation.getCurrentFrame();

                AffineTransform transformFeet = g2.getTransform();
                g2.scale(0.6, 0.6);
                g2.drawImage(feetFrame, -w / 2, -h / 2, w, h, null);
                g2.setTransform(transformFeet);
            }

            /*
            if (playerKniWalkAnimation != null && playerWalkAnimation.getCurrentFrame() != null) {
                BufferedImage bodyFrame = playerWalkAnimation.getCurrentFrame();
                g2.drawImage(bodyFrame, -w / 2, -h / 2, w, h, null);
            } else {
                g2.setColor(Color.RED);
                g2.fillOval(-w / 2, -h / 2, w, h);
            }
            g2.setTransform(transformOriginal);
             */
        }

        // Cursor de prueba
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        if (firstLayer != null) {
            g2.drawImage(firstLayer, 0, 0, mapWidth, mapHeight, null);
        }

        ///////////////////////////////////////
        //             HITBOXES              //
        ///////////////////////////////////////

        g2.setStroke(new BasicStroke(2.0f)); // Líneas más gruesas para que se vean bien

        // Hitbox del jugador (Verde)
        g2.setColor(Color.GREEN);
        Rectangle pBounds = player.getBounds();
        g2.drawRect(pBounds.x, pBounds.y, pBounds.width, pBounds.height);

        // Hitboxes de los zombies (Rojo)
        g2.setColor(Color.RED);
        for (Enemy zombie : this.controller.getState().getEnemies()) {
            if (zombie.isAlive()) {
                Rectangle zBounds = zombie.getBounds();
                g2.drawRect(zBounds.x, zBounds.y, zBounds.width, zBounds.height);
            }
        }

        // Hitboxes de las balas (Cian)
        g2.setColor(Color.CYAN);
        for (Bullet bullet : this.controller.getState().getPlayerBullets()) {
            if (bullet.isActive()) {
                Rectangle bBounds = bullet.getBounds();
                g2.drawRect(bBounds.x, bBounds.y, bBounds.width, bBounds.height);
            }
        }

        ///////////////////////////////////////
        //          EFECTO OSCURIDAD         //
        ///////////////////////////////////////

        double centerX = player.getX() + (player.getWidth() / 2);
        double centerY = player.getY() + (player.getHeight() / 2);

        Area darkness = new Area(new Rectangle2D.Double(0, 0, mapWidth, mapHeight));

        double lightRadius = 700.0; // Define distancia 
        double fovAngle = 85.0; // Define ángulo 

        double angleDegrees = -Math.toDegrees(player.getAngle());

        Arc2D.Double lightCone = new Arc2D.Double(
                centerX - lightRadius, centerY - lightRadius,
                lightRadius * 2, lightRadius * 2,
                angleDegrees - (fovAngle / 2.0), fovAngle,
                Arc2D.PIE
        );

        double personalRadius = 90.0; // Circulo personal donde se va a ver 
        Ellipse2D.Double personalCircle = new Ellipse2D.Double(
                centerX - personalRadius, centerY - personalRadius,
                personalRadius * 2, personalRadius * 2
        );

        darkness.subtract(new Area(lightCone)); // Substrae el cono de iluminación
        darkness.subtract(new Area(personalCircle)); // Substrae el circulo personal

        g2.setColor(new Color(0, 0, 0, 220)); // 245 = opacidad
        g2.fill(darkness);

        g2.setStroke(new BasicStroke(1.0f)); // Restaurar el grosor normal

        g2.translate(camX, camY);


        ///////////////////////////////////////
        //             INTERFAZ              //
        ///////////////////////////////////////

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
        GameModel.gameStatus status = controller.getStatus();
        if (status == GameModel.gameStatus.GAME_OVER) {
            showFinalMessage(g2, "JUEGO TERMINADO", Color.RED);
        } else if (status == GameModel.gameStatus.LEVEL_COMPLETED) {
            showFinalMessage(g2, "¡FELICITACIONES, HAS COMPLETADO EL NIVEL CON ÉXITO!", Color.YELLOW);
        } else if (status == GameModel.gameStatus.GAME_WON) {
            showFinalMessage(g2, "¡GANASTE!", Color.GREEN);
        } else if (status == GameModel.gameStatus.PAUSED) {
            showFinalMessage(g2, "PAUSA (ESC) para continuar)", Color.WHITE);
        } else if (status == GameModel.gameStatus.BOSS_INTRO) {
            drawBossIntro(g2);
        }

    }

    private void drawFloating(Graphics2D g2, Entity e, Color glowColor, Consumer<Graphics2D> content) {
        double t = System.currentTimeMillis() / 1000.0;
        double phase = e.getX() * 0.05;                               // desfase por objeto
        double bob = Math.sin(t * 3.0 + phase) * 4;                   // sube y baja 4 px
        double scale = 1.0 + Math.sin(t * 4.0 + phase) * 0.06;        // pulso de +-6%
        int glowAlpha = (int) (70 + Math.sin(t * 4.0 + phase) * 40);  // brillo 30..110

        int w = e.getWidth();
        int h = e.getHeight();

        AffineTransform old = g2.getTransform();
        g2.translate(e.getX() + w / 2.0, e.getY() + h / 2.0 + bob);

        g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), glowAlpha));
        g2.fillOval(-w / 2 - 6, -h / 2 - 6, w + 12, h + 12);

        g2.scale(scale, scale);
        content.accept(g2);       // cada uno dibuja lo suyo, centrado en (0, 0)

        g2.setTransform(old);
    }

    private void drawPowerUps(Graphics2D g2) {
        for (PowerUp p : this.controller.getState().getPowerUps()) {
            BufferedImage sprite = powerUpSprites.get(p.getClass());
            int w = p.getWidth();
            int h = p.getHeight();

            drawFloating(g2, p, p.getColor(), g -> {
                if (sprite != null) {
                    g.drawImage(sprite, -w / 2, -h / 2, w, h, null);
                } else {
                    g.setColor(p.getColor());
                    g.fillOval(-w / 2, -h / 2, w, h);
                    g.setColor(Color.BLACK);
                    g.drawOval(-w / 2, -h / 2, w, h);
                    g.setFont(new Font("Monospaced", Font.BOLD, 11));
                    FontMetrics fm = g.getFontMetrics();
                    g.drawString(p.getLabel(), -fm.stringWidth(p.getLabel()) / 2, fm.getAscent() / 2 - 2);
                }
            });
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

    private void drawBossIntro(Graphics2D g2) {
        int step = controller.getBossIntroStep();
        double p = controller.getBossIntroProgress();

        // el texto aparece y se desvanece de a poco
        double fade = Math.max(0.0, Math.min(1.0, Math.min(p / 0.25, (1.0 - p) / 0.25)));
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

        // temblor leve solo en "Preparate para el Jefe"
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
}