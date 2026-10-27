package controller;

import model.GameModel;
import view.GamePanel;

import javax.swing.*;

public class GameLoop {

    private static final int TICK_MS = 16; // 60 FPS -- 1000/60=16.66
    private final Timer timer;

    public GameLoop(GameModel model, GamePanel gamePanel) {
        this.timer = new Timer(TICK_MS, e -> {
            model.update();
            gamePanel.repaint();
        });
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}