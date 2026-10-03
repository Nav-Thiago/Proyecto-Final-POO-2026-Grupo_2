package view;


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

public class GamePanel extends JPanel {

    private final GameModel model;

    private Animation playerWalkAnimation;
    private Animation zombieWalkAnimation;
    private BufferedImage tankIdleSprite;
    private Animation tankWalkAnimation;
    private BufferedImage fastIdleSprite;
    private Animation fastWalkAnimation;
    private BufferedImage poisonIdleSprite;
    private Animation poisonWalkAnimation;
    private BufferedImage gunSprite;
    private BufferedImage shotGunSprite;

    // Mapas para asociar la clase del modelo con su recurso visual
    private final Map<Class<? extends Enemy>, Animation> zombieAnimations = new HashMap<>();
    private final Map<Class<? extends Enemy>, BufferedImage> zombieIdleSprites = new HashMap<>();

    private final Map<Class<? extends PowerUp>, BufferedImage> powerUpSprites = new HashMap<>();
    
   
   
   

    public  GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(30,30, 35));
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
            int frameQuantity = 19;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/player/survivor-move_handgun_" + (i+1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);

                if (in != null) {
                    frames[i] = ImageIO.read(in);
                } else {
                    System.err.println("Advertencia: No se encontró la imagen " + route);
                }
            }

            playerWalkAnimation = new Animation(150, frames);
        } catch (Exception e) {
            System.err.println("Error cargando animación del zombie: " + e.getMessage());
        }

        try {
            InputStream inGun = getClass().getResourceAsStream("/Resources/imagenes/weapons/gun.png");
            if (inGun != null) gunSprite = ImageIO.read(inGun);

            InputStream inShotgun = getClass().getResourceAsStream("/Resources/imagenes/weapons/shotgun.png");
            if (inShotgun != null) shotGunSprite = ImageIO.read(inShotgun);
        } catch (IOException e) {
            System.err.println("Error cargando sprites de armas: " + e.getMessage());
        }

        String base = "/Resources/imagenes/powerups/";
        powerUpSprites.put(Heal.class, loadImage(base + "heal.png"));
        powerUpSprites.put(Shield.class, loadImage(base + "shield.png"));
        powerUpSprites.put(DamageBoost.class, loadImage(base + "damage.png"));
        powerUpSprites.put(Ammo.class, loadImage(base + "ammo.png"));
        powerUpSprites.put(MaxAmmo.class, loadImage(base + "max_ammo.png"));

        try {
            int frameQuantity = 16;
            BufferedImage[] frames = new BufferedImage[frameQuantity];

            for (int i = 0; i < frameQuantity; i++) {
                String route = "/Resources/imagenes/entitys/zombie_normal/skeleton-move_" + (i+1) + ".png";
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

        try{
            InputStream in = getClass().getResourceAsStream("/Resources/imagenes/entitys/tank_idle.png");
            if (in != null) {
                tankIdleSprite = ImageIO.read(in);
            }
        }catch (IOException e){
            System.out.println("Error cargando sprite del tanque en reposo: " + e.getMessage());
        }

        try{
            int tankFrameQuantity = 36;
            BufferedImage[] tankFrames = new BufferedImage[tankFrameQuantity];

            for (int i = 0; i < tankFrameQuantity; i++){
                String route = "/Resources/imagenes/entitys/tank_zombie/tank_walk_" + (i + 1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);

                if (in != null) {
                    tankFrames[i] = ImageIO.read(in);
                }else{
                    System.out.println("Advertencia: No se encontro la imagen " + route);
                }
            }
            tankWalkAnimation = new Animation(100, tankFrames);

        }catch (Exception e){
            System.out.println("Error cargando animacion del tanque: " + e.getMessage());
        }

        try{
            InputStream in = getClass().getResourceAsStream("/Resources/imagenes/entitys/fast_idle.png");
            if (in != null) fastIdleSprite = ImageIO.read(in); 
        }catch (IOException e){
            System.out.println("Error cargando srpite del fast zombie en reposo: " + e.getMessage());
        }

        try{
            int fastFrameQuantity = 16;
            BufferedImage[] fastFrames = new BufferedImage[fastFrameQuantity];
            for (int i = 0; i < fastFrameQuantity; i++){
                String route = "/Resources/imagenes/entitys/fast_zombie/fast_walk_" + (i + 1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);
                if (in != null) fastFrames[i] = ImageIO.read(in); 
            }
            fastWalkAnimation = new Animation(80, fastFrames);
        }catch (Exception e){
            System.out.println("Error cargando animacion del zombie rapido: " + e.getMessage());
        }

        try{
            InputStream in = getClass().getResourceAsStream("/Resources/imagenes/entitys/poison_idle_png");
            if (in != null) poisonIdleSprite = ImageIO.read(in);
        }catch (IOException e){
            System.out.println("Error cargando sprite del poison zombie en reposo: " + e.getMessage());
        }

        try{
            int poisonFrameQuantity = 16;
            BufferedImage[] poisonFrames = new BufferedImage[poisonFrameQuantity];
            for(int i = 0; i < poisonFrameQuantity; i++){
                String route = "/Resources/imagenes/entitys/poison_zombie/poison_walk_" + (i + 1) + ".png";
                InputStream in = getClass().getResourceAsStream(route);
                if(in != null) poisonFrames[i] = ImageIO.read(in);
            }
            poisonWalkAnimation = new Animation(150, poisonFrames);
        }catch (Exception e){
            System.out.println("Error cargando animacion del zombie venenoso: " + e.getMessage());
        }

        if(zombieWalkAnimation != null){
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

        Player player = this.model.getState().getPlayer();

        // Dibuja PowerUps
        drawPowerUps(g2); // [POWER-UP] primero, para que queden debajo de zombies y jugador

        // Dibuja Armas en el suelo
        for (WeaponItem item : this.model.getState().getGroundWeapons()) {
            int x = (int) item.getX();
            int y = (int) item.getY();

            BufferedImage weaponImg = null;

            if (item.getWeapon() instanceof Gun) weaponImg = gunSprite;
            else if (item.getWeapon() instanceof ShotGun) weaponImg = shotGunSprite;

            if (weaponImg != null) {
                g2.drawImage(weaponImg, x, y, item.getWidth(), item.getHeight(), null);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(x, y, item.getWidth(), item.getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g2.drawString(item.getWeapon().getClass().getSimpleName(), x, y - 5);
            }
        }

        // Dibuja a los enemigos
        if (zombieWalkAnimation != null) {
            zombieWalkAnimation.update();
        }
        //Dibuja Zombie Tanque
        if (tankWalkAnimation != null) {
            tankWalkAnimation.update();
        }

        for(Animation anim: zombieAnimations.values()){
            anim.update();
        }
        for (Enemy zombie : this.model.getState().getEnemies()) {

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

        // Dibuja las balas del jugador
        g2.setColor(Color.ORANGE);
        for (Bullet bullet : this.model.getState().getPlayerBullets()) {
            g2.fillOval((int) bullet.getX(), (int) bullet.getY(), bullet.getWidth()-2, bullet.getHeight()-2);
        }

        // Dibuja balas de veneno
        g2.setColor(new Color(50, 205, 50));
        for (Bullet poison : this.model.getState().getPoisonBullets()) {
            g2.fillOval((int) poison.getX(), (int) poison.getY(), poison.getWidth(), poison.getHeight());
        }

        // Dibuja al jugador
        if (playerWalkAnimation != null) {
            playerWalkAnimation.update();
        }
        if (player.isAlive()) {
            if (playerWalkAnimation != null && playerWalkAnimation.getCurrentFrame() != null) {
                AffineTransform transformOriginal = g2.getTransform();

                double centerX = player.getX() + (player.getWidth() / 2.0);
                double centerY = player.getY() + (player.getHeight() / 2.0);

                g2.translate(centerX, centerY);
                g2.rotate(player.getAngle());

                int w = player.getWidth();
                int h = player.getHeight();
                BufferedImage actualFrame = playerWalkAnimation.getCurrentFrame();
                g2.drawImage(actualFrame, -w / 2, -h / 2, w, h, null);

                g2.setTransform(transformOriginal);

            } else {

                g2.setColor(Color.RED);
                g2.fillOval((int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight());
            }
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
        for (Enemy zombie : this.model.getState().getEnemies()) {
            if (zombie.isAlive()) {
                Rectangle zBounds = zombie.getBounds();
                g2.drawRect(zBounds.x, zBounds.y, zBounds.width, zBounds.height);
            }
        }

        // Hitboxes de las balas (Cian)
        g2.setColor(Color.CYAN);
        for (Bullet bullet : this.model.getState().getPlayerBullets()) {
            if (bullet.isActive()) {
                Rectangle bBounds = bullet.getBounds();
                g2.drawRect(bBounds.x, bBounds.y, bBounds.width, bBounds.height);
            }
        }

        g2.setStroke(new BasicStroke(1.0f)); // Restaurar el grosor normal
    }

    private void drawPowerUps(Graphics2D g2) {
    double t = System.currentTimeMillis() / 1000.0;

    for (PowerUp p : this.model.getState().getPowerUps()) {
        int w = p.getWidth();
        int h = p.getHeight();

        // Desfase para que no se muevan todos sincronizados
        double phase = p.getX() * 0.05;

        double bob = Math.sin(t * 3.0 + phase) * 4;              // sube y baja 4 px
        double scale = 1.0 + Math.sin(t * 4.0 + phase) * 0.06;   // pulso de +-6%
        int glowAlpha = (int) (70 + Math.sin(t * 4.0 + phase) * 40); // brillo 30..110

       // BufferedImage pSprite = null;
        BufferedImage pSprite = powerUpSprites.get(p.getClass());

        AffineTransform old = g2.getTransform();

        // Origen en el centro del power up, más el desplazamiento de flotar
        g2.translate(p.getX() + w / 2.0, p.getY() + h / 2.0 + bob);

        // Brillo de color detrás
        Color c = p.getColor();
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), glowAlpha));
        g2.fillOval(-w / 2 - 6, -h / 2 - 6, w + 12, h + 12);

        // Pulso de tamaño
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
            g2.drawString(label,
                    -fm.stringWidth(label) / 2,
                    (fm.getAscent() / 2) - 2);
        }

        g2.setTransform(old);
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
}