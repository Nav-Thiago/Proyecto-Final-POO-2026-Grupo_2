package controller;

import model.GameModel;
import model.Player;
import model.Bullet;
//import model.Enemy;
import view.GamePanel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener; 

import java.util.List;

public class GameController extends KeyAdapter implements MouseMotionListener, MouseListener {

    private final GameModel model;
    private final GamePanel gamePanel;

    public GameController(GameModel model, GamePanel gamePanel) {
        this.model = model;
        this.gamePanel = gamePanel;

        this.gamePanel.addKeyListener(this);
        this.gamePanel.addMouseListener(this);
        this.gamePanel.addMouseMotionListener(this);
    }
    // Obtener el gamePanel (para el MenuController)
    public GamePanel getGamePanel() {
        return this.gamePanel;
    }

    // TECLADO

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> this.model.getPlayer().setMoveUp(true);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> this.model.getPlayer().setMoveDown(true);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> this.model.getPlayer().setMoveLeft(true);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> this.model.getPlayer().setMoveRight(true);
            case KeyEvent.VK_Q -> this.model.getPlayer().swapWeapon();
            case KeyEvent.VK_E -> this.gamePanel.playerInteract();
            case KeyEvent.VK_ESCAPE -> this.gamePanel.togglePause();
            default -> {
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> this.model.getPlayer().setMoveUp(false);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> this.model.getPlayer().setMoveDown(false);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> this.model.getPlayer().setMoveLeft(false);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> this.model.getPlayer().setMoveRight(false);
            default -> {}
        }
    }

    // MOUSE

    @Override
    public void mouseMoved(MouseEvent e) {
        this.model.getPlayer().aimAt(e.getX(), e.getY());
    }

    @Override
    // Manteniendo algún botón pulsado
    public void mouseDragged(MouseEvent e) {
        this.model.getPlayer().aimAt(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gamePanel.requestFocusInWindow();
        
        if (e.getButton() == MouseEvent.BUTTON1) {
            this.model.getPlayer().attack(this.model.getPlayerBullets(), this.model.getEnemies());
        }
    }
    
    // Agrego debido a que tira error por el implements, se puede solucionar cambiando a: 
    // public class GameController extends MouseAdapter implements KeyListener {
    // Investigar.
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
