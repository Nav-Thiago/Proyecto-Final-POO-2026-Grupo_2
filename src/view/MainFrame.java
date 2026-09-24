package view;

import controller.GameController;
import controller.GameLoopThread;
import model.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    List<Bullet> bullets = new ArrayList<>();

    public MainFrame() {
        super("Cazador de zombies"); // Configura el título superior de la pantalla
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Asegura que el programa muera al cerrar la ventana
        this.setResizable(false); // Bloquea el redimensionamiento

        Player player = new Player(384, 284);
        player.equipWeapon(new ShotGun(20, 400, 15, 15));
        List<Enemy> enemies = new  ArrayList<>();
        enemies.add(new NormalZombie(50, 50));
        GamePanel gamePanel = new GamePanel(player, enemies, bullets);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);

        GameController controller = new GameController(player, bullets, gamePanel);
        gamePanel.addKeyListener(controller);
        gamePanel.addMouseMotionListener(controller);
        gamePanel.addMouseListener(controller);

        GameLoopThread loopThread = new GameLoopThread(gamePanel);
        loopThread.start(); // Le pide al sistema operativo que cree un nuevo hilo de procesamiento real y que dentro de ese hilo ejecute el método run()
    }
}
