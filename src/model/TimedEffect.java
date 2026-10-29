package model;

// Efecto con duración en milisegundos.
public abstract class TimedEffect implements Effect {

    private final long endTime;
    protected final GameClock clock;

    protected TimedEffect(long duration, GameClock clock) {
        this.endTime = clock.nowMillis() + duration;
        this.clock = clock;
    }

    @Override
    public boolean isExpired() {
        return clock.nowMillis() > endTime;
    }
}
