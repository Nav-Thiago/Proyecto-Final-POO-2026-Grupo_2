package view;


import model.*;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GamePanel extends JPanel {

    private final GameModel model;

    private Animation playerWalkAnimation;
    private Animation zombieWalkAnimation;
    private BufferedImage gunSprite;
    private BufferedImage shotGunSprite;

    private BufferedImage healSprite;
    private BufferedImage shieldSprite;
    private BufferedImage damageSprite;
    private BufferedImage ammoSprite;
    private BufferedImage maxAmmoSprite;

    public  GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(30,30, 35));
        setFocusable(true);
        loadSprites();
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

        
        try {
            InputStream inHeal = getClass().getResourceAsStream("/Resources/imagenes/powerups/heal.png");
            if (inHeal != null) healSprite = ImageIO.read(inHeal);

            InputStream inShield = getClass().getResourceAsStream("/Resources/imagenes/powerups/shield.png");
            if (inShield != null) shieldSprite = ImageIO.read(inShield);

            InputStream inDamage = getClass().getResourceAsStream("/Resources/imagenes/powerups/damage.png");
            if (inDamage != null) damageSprite = ImageIO.read(inDamage);

            InputStream inAmmo = getClass().getResourceAsStream("/Resources/imagenes/powerups/ammo.png");
            if (inAmmo != null) ammoSprite = ImageIO.read(inAmmo);

            InputStream inMaxAmmo = getClass().getResourceAsStream("/Resources/imagenes/powerups/max_ammo.png");
            if (inMaxAmmo != null) maxAmmoSprite = ImageIO.read(inMaxAmmo);
        } catch (IOException e) {
            System.err.println("Error cargando sprites de power-ups: " + e.getMessage());
        }

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

        for (Enemy zombie : this.model.getState().getEnemies()) {

            if (zombie.isAlive()) {
                if (zombieWalkAnimation != null && zombieWalkAnimation.getCurrentFrame() != null) {
                    AffineTransform transformOriginal = g2.getTransform();

                    double centerX = zombie.getX() + (zombie.getWidth() / 2.0);
                    double centerY = zombie.getY() + (zombie.getHeight() / 2.0);

                    g2.translate(centerX, centerY);
                    g2.rotate(zombie.getAngle());

                    int w = zombie.getWidth();
                    int h = zombie.getHeight();
                    BufferedImage actualFrame = zombieWalkAnimation.getCurrentFrame();
                    g2.drawImage(actualFrame, -w / 2, -h / 2, w, h, null);

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
        for (PowerUp p : this.model.getState().getPowerUps()) {
            int x = (int) p.getX();
            int y = (int) p.getY();

            BufferedImage pSprite = null;
            
            // Identifica el powerup
            if (p instanceof Heal) pSprite = healSprite;
            else if (p instanceof Shield) pSprite = shieldSprite;
            else if (p instanceof DamageBoost) pSprite = damageSprite;
            else if (p instanceof Ammo) pSprite = ammoSprite;
            else if (p instanceof MaxAmmo) pSprite = maxAmmoSprite;

            if (pSprite != null) {
                g2.drawImage(pSprite, x, y, p.getWidth(), p.getHeight(), null);
            } else {
                // Círculos de color
                g2.setColor(p.getColor());
                g2.fillOval(x, y, p.getWidth(), p.getHeight());
                g2.setColor(Color.BLACK);
                g2.drawOval(x, y, p.getWidth(), p.getHeight());
                
                g2.setFont(new Font("Monospaced", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String label = p.getLabel();
                g2.drawString(label,
                        x + (p.getWidth() - fm.stringWidth(label)) / 2,
                        y + (p.getHeight() + fm.getAscent()) / 2 - 2);
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

    public void togglePause() {
        this.model.togglePause();
    }

    public void playerInteract() {
        this.model.playerInteract();
    }

    public void nextLevel() {
        this.model.nextLevel();
    }
}