package model;

import java.util.function.LongSupplier;

public class GameClock {

    private final LongSupplier nanoTime;
    private final long initialMillis;
    private final long initialNanos;

    private long pausedAtNanos;
    private long totalPausedNanos;
    private boolean paused;

    public GameClock() {
        this(System::nanoTime);
    }

    // Permite controlar el tiempo en las pruebas.
    public GameClock(LongSupplier nanoTime) {
        this.nanoTime = nanoTime;
        this.initialMillis = System.currentTimeMillis();
        this.initialNanos = nanoTime.getAsLong();
    }

    public long nowMillis() {
        long currentNanos = paused
                ? pausedAtNanos
                : nanoTime.getAsLong();

        long elapsedNanos = currentNanos
                - initialNanos
                - totalPausedNanos;

        return initialMillis + elapsedNanos / 1_000_000L;
    }

    public void pause() {
        if (paused) {
            return;
        }

        pausedAtNanos = nanoTime.getAsLong();
        paused = true;
    }

    public void resume() {
        if (!paused) {
            return;
        }

        totalPausedNanos += nanoTime.getAsLong() - pausedAtNanos;
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }
}


