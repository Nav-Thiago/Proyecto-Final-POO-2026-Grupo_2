package controller;

import model.*;
import view.GamePanel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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

        if (e.getButton() == MouseEvent.BUTTON3) {
            System.out.println("X: " + model.getState().getPlayer().getX() + "| Y: " + model.getState().getPlayer().getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            this.model.getState().getPlayer().setAttacking(false);
        }
    }

    private void aimWithCamera(int screenmouseX, int screenmouseY) {
        model.Player player = this.model.getState().getPlayer();

        int screenWidth = gamePanel.getWidth();
        int screenHeight = gamePanel.getHeight();

        int mapWidth = this.model.getState().getMapWidth();
        int mapHeight = this.model.getState().getMapHeight();

        int camX = calculateCameraX(player, screenWidth, mapWidth);
        int camY = calculateCameraY(player, screenHeight, mapHeight);

        double worldMouseX = screenmouseX + camX;
        double worldMouseY = screenmouseY + camY;

        player.aimAt(worldMouseX, worldMouseY);
    }

    private int calculateCameraX(Player player, int screenWidth, int mapWidth) {
        int camX = (int) player.getX() - (screenWidth / 2) + (player.getWidth() / 2);
        return Math.max(0, Math.min(camX, mapWidth - screenWidth));
    }

    private int calculateCameraY(Player player, int screenHeight, int mapHeight) {
        int camY = (int) player.getY() - (screenHeight / 2) + (player.getHeight() / 2);
        return Math.max(0, Math.min(camY, mapHeight - screenHeight));
    }

    public void drawScene(GamePanel panel) {
        GameState state = this.model.getState();
        Player player = state.getPlayer();

        int screenWidth = panel.getWidth();
        int screenHeight = panel.getHeight();
        int mapWidth = state.getMapWidth();
        int mapHeight = state.getMapHeight();

        // Cálculo de cámara
        int camX = calculateCameraX(player, screenWidth, mapWidth);
        int camY = calculateCameraY(player, screenHeight, mapHeight);

        panel.applyCamera(camX, camY);
        panel.drawBackground(mapWidth, mapHeight);

        // Dibuja al power-up
        drawPowerUps(panel, state); // [POWER-UP] primero, para que queden debajo de zombies y jugador

        // Dibuja Armas en el suelo
        drawGroundWeapons(panel, state); // [ARMAS] segundo, para que queden debajo de zombies y jugador

        // Dibuja los zombies
        drawZombies(panel, state);

        // Dibuja a las balas venenosas
        drawPoisonBullets(panel, state);

        // Dibuja al jugador
        drawPlayer(panel, state);

        // Dibuja la primera capa del mapa
        drawFirstLayer(panel, state);

        // Dibuja la oscuridad y el cono de visión
        drawDarkness(panel, state);

        // Dibuja los disparos de las armas
        drawBulletTracers(panel, state);
        drawMuzzleFlash(panel, state);

        // Deshace la cámara
        panel.resetCamera(camX, camY);

        // Dibuja el HUD
        drawHud(panel, state);

        // Dibuja los mensajes de estado
        drawStatusMessage(panel);
    }

    private void drawPowerUps(GamePanel panel, GameState state) {
        for (PowerUp p : state.getPowerUps()) {
            panel.drawPowerUp(p.getX(), p.getY(), p.getWidth(), p.getHeight(), p.getClass().getSimpleName());
        }
    }

    private void drawGroundWeapons(GamePanel panel, GameState state) {
        for (WeaponItem item : state.getGroundWeapons()) {
            panel.drawGroundWeapon(item.getX(), item.getY(), item.getWidth(), item.getHeight(), item.getWeapon().getClass().getSimpleName());
        }
    }

    private void drawZombies(GamePanel panel, GameState state) {
        panel.updateZombieAnimations();
        for (Enemy zombie : state.getEnemies()) {
            if (zombie.isAlive()) {
                // Le pasamos zombie.isAttacking() al final
                panel.drawZombie(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight(), zombie.getAngle(), zombie.getHealth(), zombie.getClass().getSimpleName(), zombie.isAttacking());
            }
        }
    }
    
    private void drawPoisonBullets(GamePanel panel, GameState state) {
        for (Bullet poison : state.getPoisonBullets()) {
            panel.drawPoisonBullet(poison.getX(), poison.getY(), poison.getWidth(), poison.getHeight());
        }
    }

    private void drawPlayer(GamePanel panel, GameState state) {
        Player player = state.getPlayer();
        if (player.isAlive()) {
            panel.drawPlayer(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getAngle(), player.isMoving(), player.getActiveWeapon().getClass().getSimpleName());
        }
    }

    private void drawFirstLayer(GamePanel panel, GameState state) {
        panel.drawFirstLayer(state.getMapWidth(), state.getMapHeight());
    }

    private void drawDarkness(GamePanel panel, GameState state) {
        Player player = state.getPlayer();
        panel.drawDarkness(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getAngle(), state.getMapWidth(), state.getMapHeight());
    }

    private void drawHud(GamePanel panel, GameState state) {
        Player player = state.getPlayer();

        panel.drawHealth(player.getHealth(), player.getMaxHealth());

        if (player.getShield() > 0) {
            panel.drawShield(player.getShield());
        }

        if (player.getDamageBonus() > 0) {
            panel.drawDamageBonus(player.getDamageBonus());
        }

        Weapon weapon = player.getActiveWeapon();
        if (weapon.isRanged()) {
            panel.drawFireArmInfo(weapon.getClass().getSimpleName(), weapon.getActualMunition(), weapon.getMaxMunition());
        } else {
            panel.drawMeleeInfo();
        }
    }

    private void drawStatusMessage(GamePanel panel) {
        GameModel.GameStatus status = this.model.getStatus();

        if (status == GameModel.GameStatus.GAME_OVER) {
            panel.drawGameOver(model.getMatchTimeMs(), model.getBestTimeMs(), model.isNewRecord());
        } else if (status == GameModel.GameStatus.GAME_WON) {
            panel.drawGameWon(model.getMatchTimeMs(), model.getBestTimeMs(), model.isNewRecord());
        } else if (status == GameModel.GameStatus.PAUSED) {
            panel.drawPause();
        } else if (status == GameModel.GameStatus.BOSS_INTRO) {
            panel.drawBossIntro(this.model.getBossIntroStep(), this.model.getBossIntroProgress());
        }
    }

    private void drawBulletTracers(GamePanel panel, GameState state) {
        for (Bullet bullet : state.getPlayerBullets()) {
            if (bullet.isActive()) {
                panel.drawBulletTracer(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), bullet.getDirX(), bullet.getDirY(), bullet.getSpeed(), bullet.getAge(), bullet.getKind().name());
            }
        }
    }

    private void drawMuzzleFlash(GamePanel panel, GameState state) {
        Player player = state.getPlayer();
        Weapon weapon = player.getActiveWeapon();

        if (player.isAlive() && weapon.isRanged()) {
            panel.drawMuzzleFlash(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getAngle(), weapon.getClass().getSimpleName(), weapon.getLastShoot(), Weapon.MUZZLE_FORWARD, Weapon.MUZZLE_SIDE);
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
