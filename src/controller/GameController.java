package controller;

import model.GameModel;

import view.GamePanel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener; 

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
        this.model.getState().getPlayer().aimAt(e.getX(), e.getY());
    }

    @Override
    // Manteniendo algún botón pulsado
    public void mouseDragged(MouseEvent e) {
        this.model.getState().getPlayer().aimAt(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gamePanel.requestFocusInWindow();
        
        if (e.getButton() == MouseEvent.BUTTON1) {
            this.model.getState().getPlayer().attack(model.getState());
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
