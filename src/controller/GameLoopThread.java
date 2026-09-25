package controller;

import view.GamePanel;
import javax.swing.*;

import model.GameModel;

public class GameLoopThread extends Thread {

    private static final int msPorFrame = 16; // 60 FPS -- 1000/60=16.66
    private final GamePanel gamePanel;
    private final GameModel modelo;

    public GameLoopThread(GamePanel gamePanel, GameModel modelo) {
        this.gamePanel = gamePanel;
        this.modelo = modelo;
        setDaemon(true); // Hilo de fondo para apagar el programa entero
    }

    @Override
    public void run() {  // Se redefine lo que debe hacer el hilo cuando se ejecute el .start()
        while (true) { // Game Loop
            modelo.update(gamePanel.getWidth(), gamePanel.getHeight());
            gamePanel.repaint();
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
