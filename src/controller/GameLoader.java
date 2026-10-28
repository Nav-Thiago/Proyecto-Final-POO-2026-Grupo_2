package controller;

import view.GameAssets;
import view.LoadingPanel;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class GameLoader extends SwingWorker<GameAssets, String> {

    private final LoadingPanel loadingPanel;
    private final Consumer<GameAssets> onLoaded;
    private final Runnable onError;

    public GameLoader(LoadingPanel loadingPanel, Consumer<GameAssets> onLoaded, Runnable onError) {
        this.loadingPanel = loadingPanel;
        this.onLoaded = onLoaded;
        this.onError = onError;
    }

    @Override
    protected GameAssets doInBackground() {
        return new GameAssets(stage -> publish(stage));
    }

    @Override
    protected void process(List<String> stages) {
        loadingPanel.setMessage(stages.get(stages.size() - 1));
    }

    @Override
    protected void done() {
        try {
            onLoaded.accept(get());
        } catch (InterruptedException | ExecutionException e) {
            onError.run();
        }
    }
}
