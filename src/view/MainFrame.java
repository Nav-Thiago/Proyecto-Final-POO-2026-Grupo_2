package view;

import controller.GameController;
import controller.GameLoopThread;
import model.Enemy;
import model.Player;
import model.NormalZombie;
import model.Projectile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    List<Projectile> projectiles = new ArrayList<>();

    public MainFrame() {
        super("Cazador de zombies"); // Configura el título superior de la pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Asegura que el programa muera al cerrar la ventana
        setResizable(false); // Bloquea el redimensionamiento

        Player player = new Player(384, 284);
        Enemy zombie = new NormalZombie(50, 50);
        GamePanel gamePanel = new GamePanel(player, zombie, projectiles);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);

        GameController controller = new GameController(player, projectiles, gamePanel);
        gamePanel.addKeyListener(controller);
        gamePanel.addMouseMotionListener(controller);
        gamePanel.addMouseListener(controller);

        GameLoopThread loopThread = new GameLoopThread(gamePanel);
        loopThread.start(); // Le pide al sistema operativo que cree un nuevo hilo de procesamiento real y que dentro de ese hilo ejecute el método run()
    }
}
