package controller;

import model.*;
import view.*;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.Timer;

public class MenuController implements MenuActions{

    private MainFrame mainFrame;
    private GameController controller;

    private CardLayout cardLayout;
    private JPanel mainContainer;

    public MenuController() {
        mainFrame = new MainFrame();
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        SplashUniversity uniSplash = new SplashUniversity();
        SplashSubject subjectSplash = new SplashSubject();
        MenuPanel menu = new MenuPanel();
        OptionsPanel option = new OptionsPanel(this);

        menu.getPlayButton().addActionListener(e -> startGame());
        menu.getOptionButton().addActionListener(e -> optionMenu());
        menu.getExitButton().addActionListener(e -> System.exit(0));
        

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
        Player player = new Player(128, 3040);
        player.equipWeapon(new Handgun(20, 400, 20, 20));

        // Dimensiones del mapa 
        int mapCols = 200;
        int mapRows = 200;
        int widthPixels = mapCols * 32;
        int heightPixels = mapRows * 32;

        GameState state = new GameState(player, widthPixels, heightPixels);
        boolean[][] matrix = MapLoader.loadCollisionMatrix("/Resources/mapa_bosque_ultimo-111026/mapa_bosque_NWB.tmj", mapCols, mapRows);
        state.setCollisionMatrix(matrix);
        state.getGroundWeapons().add(new WeaponItem(1753, 1415, new ShotGun(30, 800, 7, 7)));
        state.getGroundWeapons().add(new WeaponItem(2807, 323, new Rifle(30, 200, 30, 30)));

        GameModel model = new GameModel(state);
        this.controller = new GameController(model);
        GamePanel gamePanel = new GamePanel(this.controller);
        this.controller.setGamePanel(gamePanel);

        //gamePanel.addKeyListener(controller);
        //gamePanel.addMouseMotionListener(controller);
        //gamePanel.addMouseListener(controller);

        mainContainer.add(gamePanel, "GAME");
        mainContainer.revalidate();
        cardLayout.show(mainContainer, "GAME");

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

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
