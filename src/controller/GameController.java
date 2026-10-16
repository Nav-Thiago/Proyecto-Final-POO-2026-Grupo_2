package controller;

import model.*;
import view.GamePanel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

public class GameController extends KeyAdapter implements MouseMotionListener, MouseListener {

    private final GameModel model;
    private GamePanel gamePanel;

    public GameController(GameModel model) {
        this.model = model;
    }

    public void setGamePanel (GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gamePanel.addKeyListener(this);
        this.gamePanel.addMouseListener(this);
        this.gamePanel.addMouseMotionListener(this);
    }

    // TECLADO
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> this.model.getState().getPlayer().setMoveUp(true);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> this.model.getState().getPlayer().setMoveDown(true);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> this.model.getState().getPlayer().setMoveLeft(true);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> this.model.getState().getPlayer().setMoveRight(true);
            case KeyEvent.VK_Q -> this.model.getState().getPlayer().swapWeapon();
            case KeyEvent.VK_E -> this.model.playerInteract();
            case KeyEvent.VK_ESCAPE -> this.model.togglePause();
            default -> {
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> this.model.getState().getPlayer().setMoveUp(false);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> this.model.getState().getPlayer().setMoveDown(false);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> this.model.getState().getPlayer().setMoveLeft(false);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> this.model.getState().getPlayer().setMoveRight(false);
            default -> {}
        }
    }

    // MOUSE

    private void aimWithCamera(int screenmouseX, int screenmouseY) {
        model.Player player = this.model.getState().getPlayer();

        int screenWidth = gamePanel.getWidth();
        int screenHeight = gamePanel.getHeight();

        int mapWidth = this.model.getState().getMapWidth();
        int mapHeight = this.model.getState().getMapHeight();

        int camX = (int) player.getX() - (screenWidth / 2) + (player.getWidth() / 2);
        int camY = (int) player.getY() - (screenHeight / 2) + (player.getHeight() / 2);

        // Cálculo para no irse del mapa
        camX = Math.max(0, Math.min(camX, mapWidth - screenWidth));
        camY = Math.max(0, Math.min(camY, mapHeight - screenHeight));

        double worldMouseX = screenmouseX + camX;
        double worldMouseY = screenmouseY + camY;

        player.aimAt(worldMouseX, worldMouseY);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //this.model.getState().getPlayer().aimAt(e.getX(), e.getY());
        aimWithCamera(e.getX(), e.getY());
    }

    @Override
    // Manteniendo algún botón pulsado
    public void mouseDragged(MouseEvent e) {
        //this.model.getState().getPlayer().aimAt(e.getX(), e.getY());
        aimWithCamera(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gamePanel.requestFocusInWindow();

        if (e.getButton() == MouseEvent.BUTTON1) {
            this.model.getState().getPlayer().attack(model.getState());
            this.model.getState().getPlayer().setAttacking(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            this.model.getState().getPlayer().setAttacking(false);
        }
    }

    public void drawScene(GamePanel panel, Graphics2D g2) {
        GameState state = this.model.getState();
        Player player = state.getPlayer();

        int screenWidth = panel.getWidth();
        int screenHeight = panel.getHeight();
        int mapWidth = state.getMapWidth();
        int mapHeight = state.getMapHeight();

        // Cálculo de cámara
        int camX = (int) player.getX() - (screenWidth / 2) + (player.getWidth() / 2);
        int camY = (int) player.getY() - (screenHeight / 2) + (player.getHeight() / 2);

        // Cálculo para no irse del mapa
        camX = Math.max(0, Math.min(camX, mapWidth - screenWidth));
        camY = Math.max(0, Math.min(camY, mapHeight - screenHeight));

        panel.applyCamera(g2, camX, camY);
        panel.drawBackground(g2, mapWidth, mapHeight);

        // Dibuja al power-up
        drawPowerUps(panel, g2, state); // [POWER-UP] primero, para que queden debajo de zombies y jugador

        // Dibuja Armas en el suelo
        drawGroundWeapons(panel, g2, state); // [ARMAS] segundo, para que queden debajo de zombies y jugador

        // Dibuja los zombies
        drawZombies(panel, g2, state);

        // Dibuja a las balas venenosas
        drawPoisonBullets(panel, g2, state);

        // Dibuja al jugador
        drawPlayer(panel, g2, state);

        // Dibuja la primera capa del mapa
        drawFirstLayer(panel, g2, state);

        // Dibuja la oscuridad y el cono de visión
        drawDarkness(panel, g2, state);

        // Dibuja los disparos de las armas
        drawBulletTracers(panel, g2, state);
        drawMuzzleFlash(panel, g2, state);

        // Deshace la cámara
        panel.resetCamera(g2, camX, camY);

        // Dibuja el HUD
        drawHud(panel, g2, state);

        // Dibuja los mensajes de estado
        drawStatusMessage(panel, g2);
    }

    private void drawPowerUps(GamePanel panel, Graphics2D g2, GameState state) {
        for (PowerUp p : state.getPowerUps()) {
            panel.drawPowerUp(g2, p.getX(), p.getY(), p.getWidth(), p.getHeight(), p.getClass().getSimpleName());
        }
    }

    private void drawGroundWeapons(GamePanel panel, Graphics2D g2, GameState state) {
        for (WeaponItem item : state.getGroundWeapons()) {
            panel.drawGroundWeapon(g2, item.getX(), item.getY(), item.getWidth(), item.getHeight(), item.getWeapon().getClass().getSimpleName());
        }
    }

    private void drawZombies(GamePanel panel, Graphics2D g2, GameState state) {
        panel.updateZombieAnimations();
        for (Enemy zombie : state.getEnemies()) {
            if (zombie.isAlive()) {
                panel.drawZombie(g2, zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight(), zombie.getAngle(), zombie.getHealth(), zombie.getClass().getSimpleName());
            }
        }
    }

    private void drawPoisonBullets(GamePanel panel, Graphics2D g2, GameState state) {
        for (Bullet poison : state.getPoisonBullets()) {
            panel.drawPoisonBullet(g2, poison.getX(), poison.getY(), poison.getWidth(), poison.getHeight());
        }
    }

    private void drawPlayer(GamePanel panel, Graphics2D g2, GameState state) {
        Player player = state.getPlayer();
        if (player.isAlive()) {
            panel.drawPlayer(g2, player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getAngle(), player.isMoving(), player.getActiveWeapon().getClass().getSimpleName());
        }
    }

    private void drawFirstLayer(GamePanel panel, Graphics2D g2, GameState state) {
        panel.drawFirstLayer(g2, state.getMapWidth(), state.getMapHeight());
    }

    private void drawDarkness(GamePanel panel, Graphics2D g2, GameState state) {
        Player player = state.getPlayer();
        panel.drawDarkness(g2, player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getAngle(), state.getMapWidth(), state.getMapHeight());
    }

    private void drawHud(GamePanel panel, Graphics2D g2, GameState state) {
        Player player = state.getPlayer();

        panel.drawHealth(g2, player.getHealth(), player.getMaxHealth());

        if (player.getShield() > 0) {
            panel.drawShield(g2, player.getShield());
        }

        if (player.getDamageBonus() > 0) {
            panel.drawDamageBonus(g2, player.getDamageBonus());
        }

        Weapon activeWeapon = player.getActiveWeapon();
        if (activeWeapon instanceof FireArm fa) {
            panel.drawFireArmInfo(g2, fa.getClass().getSimpleName(), fa.getActualMunition(), fa.getMaxMunition());
        } else if (activeWeapon instanceof MeleeWeapon) {
            panel.drawMeleeInfo(g2);
        }
    }

    private void drawStatusMessage(GamePanel panel, Graphics2D g2) {
        GameModel.gameStatus status = this.model.getStatus();

        if (status == GameModel.gameStatus.GAME_OVER) {
            panel.drawGameOver(g2);
        } else if (status == GameModel.gameStatus.LEVEL_COMPLETED) {
            panel.drawLevelCompleted(g2);
        } else if (status == GameModel.gameStatus.GAME_WON) {
            panel.drawGameWon(g2);
        } else if (status == GameModel.gameStatus.PAUSED) {
            panel.drawPause(g2);
        } else if (status == GameModel.gameStatus.BOSS_INTRO) {
            panel.drawBossIntro(g2, this.model.getBossIntroStep(), this.model.getBossIntroProgress());
        }
    }

    private void drawBulletTracers(GamePanel panel, Graphics2D g2, GameState state) {
        for (Bullet bullet : state.getPlayerBullets()) {
            if (bullet.isActive()) {
                panel.drawBulletTracer(g2, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), bullet.getDirX(), bullet.getDirY(), bullet.getSpeed(), bullet.getAge(), bullet.getKind().name());
            }
        }
    }

    private void drawMuzzleFlash(GamePanel panel, Graphics2D g2, GameState state) {
        Player player = state.getPlayer();
        Weapon weapon = player.getActiveWeapon();

        if (player.isAlive() && weapon instanceof FireArm) {
            panel.drawMuzzleFlash(g2, player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getAngle(), weapon.getClass().getSimpleName(), weapon.getLastShoot(), Weapon.MUZZLE_FORWARD, Weapon.MUZZLE_SIDE);
        }
    }

    // Agrego debido a que tira error por el implements, se puede solucionar cambiando a: 
    // public class GameController extends MouseAdapter implements KeyListener {
    // Investigar.
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
