package controller;

import model.GameModel;
import view.GamePanel;

import javax.swing.*;

public class GameLoopThread extends Thread {

    private static final int msPorFrame = 16; // 60 FPS -- 1000/60=16.66
    private final GamePanel gamePanel;
    private final GameModel model;

    public GameLoopThread(GamePanel gamePanel, GameModel model) {
        this.gamePanel = gamePanel;
        this.model = model;
        setDaemon(true); // Hilo de fondo para apagar el programa entero
    }

    @Override
    public void run() {  // Se redefine lo que debe hacer el hilo cuando se ejecute el .start()
        while (true) { // Game Loop
            model.update();
            SwingUtilities.invokeLater(gamePanel :: repaint);
            try {
                Thread.sleep(msPorFrame); // Se pide al hilo que "descanse" por 16 ms para que el bucle no use el 100% del procesador
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Maneja la excepción por si se cierra cuando esta "descansando"
                break;
            }
        }
    }
}
