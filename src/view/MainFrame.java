package view;

import controller.GameController;
import controller.GameLoopThread;
import model.Enemy;
import model.GameModel;
import model.Player;
import model.NormalZombie;
import model.Bullet;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainFrame extends JFrame {

    List<Enemy> enemies = new CopyOnWriteArrayList<>();
    List<Bullet> bulletPlayer = new CopyOnWriteArrayList<>();
    List<Bullet> poisonZombie = new CopyOnWriteArrayList<>();

    public MainFrame() {
        super("Cazador de zombies"); // Configura el título superior de la pantalla
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Asegura que el programa muera al cerrar la ventana
        this.setResizable(false); // Bloquea el redimensionamiento

        Player player = new Player(384, 284);
        List<Enemy> enemies = new  ArrayList<>();
        enemies.add(new NormalZombie(50, 50));
        GameModel modelo = new GameModel(player, enemies, bulletPlayer, poisonZombie);
        GamePanel gamePanel = new GamePanel(modelo);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);

        GameController controller = new GameController(player, bulletPlayer, gamePanel, modelo);
        gamePanel.addKeyListener(controller);
        gamePanel.addMouseMotionListener(controller);
        gamePanel.addMouseListener(controller);

        GameLoopThread loopThread = new GameLoopThread(gamePanel, modelo);
        loopThread.start(); // Le pide al sistema operativo que cree un nuevo hilo de procesamiento real y que dentro de ese hilo ejecute el método run()
    }
}
