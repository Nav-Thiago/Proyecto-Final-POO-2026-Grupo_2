package controller;

import view.GamePanel;
import view.MainFrame;
import view.SplashUniversity;
import view.SplashSubject;
import view.MenuPanel;
import view.OptionsPanel;

import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.Timer;

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
        AudioManager.playMusic("Resources/audio/soundtrack2.wav");
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
        GameController gameController = new GameController();
        GamePanel game = gameController.getGamePanel();
        mainContainer.add(game, "GAME");
        mainContainer.revalidate();
        cardLayout.show(mainContainer, "GAME");
        gameController.startGame();
    }

    public void changeResolution(int width, int height) {
        mainFrame.setSize(width, height);
        mainFrame.setLocationRelativeTo(null);
        mainContainer.revalidate();
        mainContainer.repaint();
    }
}
