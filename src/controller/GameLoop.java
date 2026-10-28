package controller;

import model.GameModel;
import model.SoundEvent;
import view.GamePanel;

import javax.swing.*;

public class GameLoop {

    private static final int TICK_MS = 16; // 60 FPS -- 1000/60=16.66
    private final Timer timer;

    public GameLoop(GameModel model, GamePanel gamePanel) {
        this.timer = new Timer(TICK_MS, e -> {
            model.update();
            SoundEvent sound;
            while ((sound = model.getState().pollSound()) != null) {
                AudioManager.playSfx(sound);
            }
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