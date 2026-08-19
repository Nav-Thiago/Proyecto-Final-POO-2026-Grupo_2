package controller;

import model.Player;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController extends KeyAdapter {

    private final Player player;

    public GameController( Player player) {
        this.player = player;
    }

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
}
