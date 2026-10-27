package view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GameAssets {

    private Animation playerFeetAnimation;
    private BufferedImage backgroundForest;
    private BufferedImage firstLayer;

    private final Map<String, Animation> playerAnimations = new HashMap<>();
    private final Map<String, Animation> zombieAnimations = new HashMap<>();
    private final Map<String, BufferedImage> zombieIdleSprites = new HashMap<>();
    private final Map<String, Animation> zombieAttackAnimations = new HashMap<>();
    private final Map<String, Animation> zombieSpitAnimations = new HashMap<>();
    private final Map<String, BufferedImage> powerUpSprites = new HashMap<>();
    private final Map<String, BufferedImage> weaponSprites = new HashMap<>();

    // Recibe el nombre de cada etapa de la carga para poder mostrarla
    public GameAssets(Consumer<String> onStage) {
        loadSprites(onStage);
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

    private void loadSprites(Consumer<String> onStage) {
        onStage.accept("Cargando el mapa...");
        backgroundForest = loadImage("/Resources/mapa_bosque_ultimo-111026/fondo_bosque.png");
        firstLayer = loadImage("/Resources/mapa_bosque_ultimo-111026/primer_plano.png");

        onStage.accept("Cargando al jugador...");
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

        onStage.accept("Cargando power-ups y armas...");
        String base = "/Resources/imagenes/powerups/";
        powerUpSprites.put("Heal", loadImage(base + "heal.png"));
        powerUpSprites.put("Shield", loadImage(base + "shield.png"));
        powerUpSprites.put("DamageBoost", loadImage(base + "damage.png"));
        powerUpSprites.put("Ammo", loadImage(base + "ammo.png"));
        powerUpSprites.put("MaxAmmo", loadImage(base + "max_ammo.png"));

        weaponSprites.put("Handgun", loadImage("/Resources/imagenes/weapons/gun.png"));
        weaponSprites.put("Shotgun", loadImage("/Resources/imagenes/weapons/shotgun.png"));
        weaponSprites.put("Rifle", loadImage("/Resources/imagenes/weapons/rifle.png"));

        onStage.accept("Cargando zombies...");
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

        onStage.accept("Cargando al jefe final...");
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
            zombieAnimations.put("FinalBoss", new Animation(100, walkFrames));

            int bossSpitFramesCount = 36;
            BufferedImage[] bossSpitFrames = new BufferedImage[bossSpitFramesCount];
            for(int i = 0; i < bossSpitFramesCount; i++){
                String route = "/Resources/imagenes/entitys/zombie_forest/boss_zombie/boss_spit/boss_spit_" + (i+1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);
                if (in != null) bossSpitFrames[i] = ImageIO.read(in);
            }
            zombieSpitAnimations.put("FinalBoss", new Animation(150, bossSpitFrames));

            int attackFramesCount = 36;
            BufferedImage[] attackFrames = new BufferedImage[attackFramesCount];
            for(int i = 0; i < attackFramesCount; i++){
                String route = "/Resources/imagenes/entitys/zombie_forest/boss_zombie/boss_attack_" + (i+1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);
                if (in != null) attackFrames[i] = ImageIO.read(in);
            }
            zombieAttackAnimations.put("FinalBoss", new Animation(150, attackFrames));

        } catch (Exception e) {
            System.err.println("Error cargando animaciones del FinalBoss: " + e.getMessage());
        }
    }

    public BufferedImage getBackgroundForest() {
        return backgroundForest;
    }

    public BufferedImage getFirstLayer() {
        return firstLayer;
    }

    public Animation getPlayerFeetAnimation() {
        return playerFeetAnimation;
    }

    public Map<String, Animation> getPlayerAnimations() {
        return playerAnimations;
    }

    public Map<String, Animation> getZombieAnimations() {
        return zombieAnimations;
    }

    public Map<String, BufferedImage> getZombieIdleSprites() {
        return zombieIdleSprites;
    }

    public Map<String, Animation> getZombieAttackAnimations() {
        return zombieAttackAnimations;
    }

    public Map<String, Animation> getZombieSpitAnimations() {
        return zombieSpitAnimations;
    }

    public Map<String, BufferedImage> getPowerUpSprites() {
        return powerUpSprites;
    }

    public Map<String, BufferedImage> getWeaponSprites() {
        return weaponSprites;
    }
}
