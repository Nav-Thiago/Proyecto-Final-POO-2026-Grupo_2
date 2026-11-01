package controller;

import model.*;
import view.*;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;

import javax.swing.*;

public class MenuController {

    private final MainFrame mainFrame;
    private GameController controller;
    private GameLoop gameLoop;

    private GameModel activeModel;
    private GamePanel activeGamePanel;
    private PausePanel pausePanel;

    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private GameAssets cachedAssets;

    private final LoadingPanel loadingPanel = new LoadingPanel();
    private final HistoryPanel history = new HistoryPanel();
    private final GameOverPanel gameOver = new GameOverPanel();

    private final MatchDAO matchDAO;

    public MenuController(MatchDAO matchDAO) {
        this.matchDAO = matchDAO;
        mainFrame = new MainFrame();
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        SplashUniversity uniSplash = new SplashUniversity();
        SplashSubject subjectSplash = new SplashSubject();
        MenuPanel menu = new MenuPanel();
        OptionsPanel option = new OptionsPanel();

        menu.getPlayButton().addActionListener(e -> startGame());
        menu.getOptionButton().addActionListener(e -> showOptionsMenu());
        menu.getExitButton().addActionListener(e -> System.exit(0));
        menu.getHistoryButton().addActionListener(e -> showHistory());
        history.getBackButton().addActionListener(e -> showMainMenu());
        gameOver.getBackButton().addActionListener(e -> backToMenu());
        option.getResolutionBox().addActionListener(e -> onResolutionSelected((String) option.getResolutionBox().getSelectedItem()));
        option.getBackButton().addActionListener(e -> showMainMenu());

        mainContainer.add(uniSplash, "SPLASH_UNI");
        mainContainer.add(subjectSplash, "SPLASH_SUB");
        mainContainer.add(menu, "MENU");
        mainContainer.add(option, "OPTIONS");
        mainContainer.add(loadingPanel, "LOADING");
        mainContainer.add(history, "HISTORY");
        mainContainer.add(gameOver, "GAME_OVER");

        mainFrame.add(mainContainer);

        mainFrame.setVisible(true);

        // Loop de cards (Universidad -> Materia -> Menu)
        showUniversitySplash();
    }

    private void buildGame(GameAssets assets) {
        cachedAssets = assets;

        if (gameLoop != null) {
            gameLoop.stop();
        }

        if (activeGamePanel != null) {
            mainContainer.remove(activeGamePanel);
        }

        activeModel = GameFactory.createGame(matchDAO);

        controller = new GameController(activeModel, this::toggleGamePause);

        activeGamePanel = new GamePanel(controller, assets);
        controller.setGamePanel(activeGamePanel);

        pausePanel = new PausePanel(this::resumeGame, this::abandonGame);

        pausePanel.setVisible(false);

        activeGamePanel.setLayout(new BorderLayout());
        activeGamePanel.add(pausePanel, BorderLayout.CENTER);

        mainContainer.add(activeGamePanel, "GAME");
        mainContainer.revalidate();
        cardLayout.show(mainContainer, "GAME");

        gameLoop = new GameLoop(activeModel, activeGamePanel, () -> showGameOver(activeModel));
        gameLoop.start();

        activeGamePanel.requestFocusInWindow();
    }

    private void startGame() {
        AudioManager.stopMusic();

        if (cachedAssets != null) {
            buildGame(cachedAssets);
            return;
        }

        loadingPanel.setMessage("Cargando...");
        cardLayout.show(mainContainer, "LOADING");

        new GameLoader(loadingPanel, assets -> buildGame(assets), () -> onLoadFailed())
                .execute();
    }

    private void showMainMenu() {
        cardLayout.show(mainContainer, "MENU");
    }

    private void showOptionsMenu() {
        cardLayout.show(mainContainer, "OPTIONS");
    }

    private void showHistory() {
        List<MatchRecord> records = matchDAO.fastestVictories(10);

        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            MatchRecord record = records.get(i);
            rows.add(new Object[] {i + 1, record.getDate().toString(), record.getSurvivalTimeMs(), record.getZombiesKilled(),
                    record.getDamageDealt(), record.getDamageTaken()
            });
        }

        history.showRows(rows);
        cardLayout.show(mainContainer, "HISTORY");
    }

    private void showGameOver(GameModel model) {
        boolean victory = model.getStatus() == GameModel.GameStatus.GAME_WON;
        gameOver.showResult(victory, model.getMatchTimeMs(), model.getBestTimeMs(), model.isNewRecord());
        cardLayout.show(mainContainer, "GAME_OVER");
    }

    private void showUniversitySplash() {
        AudioManager.playMusic("/Resources/audio/soundtrack2.wav");
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

    private void backToMenu() {
        AudioManager.playMusic("/Resources/audio/soundtrack2.wav");
        AudioManager.setVolume(50);
        showMainMenu();
    }

    private void onLoadFailed() {
        JOptionPane.showMessageDialog(mainFrame,"No se pudieron cargar los recursos del juego", "Error", JOptionPane.ERROR_MESSAGE);
        showMainMenu();
    }

    private void changeResolution(int width, int height) {
        mainFrame.setSize(width, height);
        mainFrame.setLocationRelativeTo(null);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private void onResolutionSelected(String selected) {
        if (selected == null) return;

        String[] size = selected.split(" x ");
        changeResolution(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
    }

    private void toggleGamePause() {
        if (activeModel == null) {
            return;
        }
        if (activeModel.getStatus() == GameModel.GameStatus.PAUSED) {
            resumeGame();
            return;
        }
        activeModel.pause();
        if (activeModel.getStatus() != GameModel.GameStatus.PAUSED) {
            return;
        }

        gameLoop.stop();
        pausePanel.setVisible(true);

        activeGamePanel.revalidate();
        activeGamePanel.repaint();
        pausePanel.requestFocusInWindow();
    }

    private void resumeGame() {
        if (activeModel == null || activeModel.getStatus() != GameModel.GameStatus.PAUSED) {
            return;
        }

        pausePanel.setVisible(false);
        activeModel.resume();

        activeGamePanel.revalidate();
        activeGamePanel.repaint();
        activeGamePanel.requestFocusInWindow();

        gameLoop.start();
    }

    private void abandonGame() {
        if (activeModel == null || activeModel.getStatus() != GameModel.GameStatus.PAUSED) {
            return;
        }

        Object[] choices = { "Abandonar", "Cancelar" };

        int choice = JOptionPane.showOptionDialog(mainFrame, "¿Abandonar la partida? Se perderá el progreso actual.", "Abandonar partida", JOptionPane.YES_NO_OPTION, 
        JOptionPane.QUESTION_MESSAGE, null, choices, choices[1]);

        if (choice != 0) {
            return;
        }

        gameLoop.stop();
        mainContainer.remove(activeGamePanel);

        activeModel = null;
        activeGamePanel = null;
        pausePanel = null;
        gameLoop = null;
        controller = null;

        mainContainer.revalidate();
        mainContainer.repaint();

        showMainMenu();
        AudioManager.playMusic("/Resources/audio/soundtrack2.wav");
    }

}
