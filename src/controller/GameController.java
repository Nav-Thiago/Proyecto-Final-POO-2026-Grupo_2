package controller;

import model.Player;
import model.Proyectil;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener; 

import java.util.List;

public class GameController extends KeyAdapter implements MouseMotionListener, MouseListener {

    private final Player player;
    private List<Proyectil> proyectiles;

    public GameController( Player player, List<Proyectil> proyectiles) {
        this.player = player;
        this.proyectiles = proyectiles;
    }

    // TECLADO

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> player.setMoveUp(true);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> player.setMoveDown(true);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> player.setMoveLeft(true);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> player.setMoveRight(true);
            default -> {
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> player.setMoveUp(false);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> player.setMoveDown(false);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> player.setMoveLeft(false);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> player.setMoveRight(false);
            default -> {}
        }
    }

    // MOUSE

    @Override
    public void mouseMoved(MouseEvent e) {
        player.apuntarHacia(e.getX(), e.getY());
    }

    @Override
    // Manteniendo algún botón pulsado
    public void mouseDragged(MouseEvent e) {
        player.apuntarHacia(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            System.out.println("Disparo efectuado");
            Proyectil bala = player.disparar();
            if (bala != null) {
                proyectiles.add(bala);
            }
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
