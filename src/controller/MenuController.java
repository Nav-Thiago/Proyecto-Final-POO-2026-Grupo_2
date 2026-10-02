package controller;

import view.GamePanel;
import view.MainFrame;
import view.SplashUniversity;
import view.SplashSubject;
import view.MenuPanel;
import view.OptionsPanel;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import model.Bullet;
import model.Enemy;
import model.GameModel;
import model.Gun;
import model.NormalZombie;
import model.Player;
import model.ShotGun;
import model.WeaponItem;

public class MenuController implements MenuActions{

    private MainFrame mainFrame;

    private CardLayout cardLayout;
    private JPanel mainContainer;

    public MenuController() {
        mainFrame = new MainFrame();
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        SplashUniversity uniSplash = new SplashUniversity();
        SplashSubject subjectSplash = new SplashSubject();
        MenuPanel menu = new MenuPanel(this);
        OptionsPanel option = new OptionsPanel(this);
        

        mainContainer.add(uniSplash, "SPLASH_UNI");
        mainContainer.add(subjectSplash, "SPLASH_SUB");
        mainContainer.add(menu, "MENU");
        mainContainer.add(option, "OPTIONS");

        mainFrame.add(mainContainer);

        mainFrame.setVisible(true);

        // Loop de cards (Universidad -> Materia -> Menu)
        showUniversitySplash();
    }

    private void showUniversitySplash() {
        AudioManager.playMusic("/Resources/audio/soundtrack1.wav");
        AudioManager.setVolume(50);
        cardLayout.show(mainContainer, "SPLASH_UNI");

        Timer timer = new Timer(2000, e -> {
            showSubjectSplash();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showSubjectSplash() {
        cardLayout.show(mainContainer, "SPLASH_SUB");

        Timer timer = new Timer(2000, e -> {
            showMainMenu();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showMainMenu() {
        cardLayout.show(mainContainer, "MENU");
        //AudioManager.playMusic("Resources/audio/soundtrack1.wav");
        //AudioManager.setVolume(50);
    }
    // Implemento el método de la interfaz "MenuActions" para poder utilizarlo en "MenuPanel"
    @Override 
    public void backToMenu() {
        showMainMenu();
    }

    private void showOptionsMenu() {
        cardLayout.show(mainContainer, "OPTIONS");
    }
    @Override 
    public void optionMenu() {
        showOptionsMenu();
    }

    @Override 
    public void startGame() {
        AudioManager.stopMusic();
        Player player = new Player(384, 284);
        player.equipWeapon(new Gun(20, 400, 15, 15));

        List<Enemy> enemies = new  ArrayList<>();
        enemies.add(new NormalZombie(50, 50));

        List<Bullet> playerBullets = new ArrayList<>();
        List<Bullet> poisonBullets = new ArrayList<>();

        List<WeaponItem> groundWeapons = new ArrayList<>();
        groundWeapons.add(new WeaponItem(500, 600, new ShotGun(30, 800, 5, 5)));

        GameModel model = new GameModel(player, enemies, playerBullets, poisonBullets, groundWeapons);
        GamePanel gamePanel = new GamePanel(model);

        new GameController(model, gamePanel);
        //gamePanel.addKeyListener(controller);
        //gamePanel.addMouseMotionListener(controller);
        //gamePanel.addMouseListener(controller);

        mainContainer.add(gamePanel, "GAME");
        mainContainer.revalidate();
        cardLayout.show(mainContainer, "GAME");

        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

        gamePanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override 
            public void componentShown(java.awt.event.ComponentEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });

        GameLoopThread loopThread = new GameLoopThread(gamePanel, model);
        loopThread.start(); // Le pide al sistema operativo que cree un nuevo hilo de procesamiento real y que dentro de ese hilo ejecute el método run()
    }

    public void changeResolution(int width, int height) {
        mainFrame.setSize(width, height);
        mainFrame.setLocationRelativeTo(null);
        mainContainer.revalidate();
        mainContainer.repaint();
    }
}
