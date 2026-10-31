package controller;

import model.*;
import view.GamePanel;

import java.awt.event.*;

public class GameController extends MouseAdapter implements MouseMotionListener, MouseListener {

    private final GameModel model;
    private GamePanel gamePanel;
    private final SceneDrawer sceneDrawer;
    private final Runnable onPause;

    public GameController(GameModel model, Runnable onPause) {
        this.model = model;
        this.onPause = onPause;
        this.sceneDrawer = new SceneDrawer(model);
    }

    public void setGamePanel (GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gamePanel.addMouseListener(this);
        this.gamePanel.addMouseMotionListener(this);
        this.gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                onKeyReleased(e);
            }
        });
    }

    // TECLADO
    public void onKeyPressed(KeyEvent e) {
        if (model.getStatus() != GameModel.GameStatus.PLAYING) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> this.model.getState().getPlayer().setMoveUp(true);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> this.model.getState().getPlayer().setMoveDown(true);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> this.model.getState().getPlayer().setMoveLeft(true);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> this.model.getState().getPlayer().setMoveRight(true);
            case KeyEvent.VK_Q -> this.model.getState().getPlayer().swapWeapon();
            case KeyEvent.VK_E -> this.model.playerInteract();
            default -> {}
        }
    }

    public void onKeyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            onPause.run();
            return;
        }
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
        aimWithCamera(e.getX(), e.getY());
    }

    @Override
    // Manteniendo algún botón pulsado
    public void mouseDragged(MouseEvent e) {
        aimWithCamera(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (model.getStatus() != GameModel.GameStatus.PLAYING) {
            return;
        }

        gamePanel.requestFocusInWindow();

        if (e.getButton() == MouseEvent.BUTTON1) {
            Player player = model.getState().getPlayer();
            player.attack(model.getState());
            player.setAttacking(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            this.model.getState().getPlayer().setAttacking(false);
        }
    }

    private void aimWithCamera(int screenMouseX, int screenMouseY) {
        // Se agrega para que el jugador no pueda apuntar con el mouse cuando el juego está en pausa o en la intro del jefe
        if (model.getStatus() != GameModel.GameStatus.PLAYING) {
            return;
        }
        model.Player player = this.model.getState().getPlayer();

        int screenWidth = gamePanel.getWidth();
        int screenHeight = gamePanel.getHeight();

        int mapWidth = this.model.getState().getMapWidth();
        int mapHeight = this.model.getState().getMapHeight();

        int camX = Camera.calculateX(player, screenWidth, mapWidth);
        int camY = Camera.calculateY(player, screenHeight, mapHeight);

        double worldMouseX = screenMouseX + camX;
        double worldMouseY = screenMouseY + camY;

        player.aimAt(worldMouseX, worldMouseY);
    }

    public void drawScene(GamePanel panel) {
        sceneDrawer.drawScene(panel);
    }
}
