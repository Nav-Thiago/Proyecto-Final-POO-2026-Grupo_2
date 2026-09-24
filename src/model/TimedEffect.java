package model;

/** Efecto con duración en milisegundos. */
public abstract class TimedEffect implements Effect {

    private final long endTime;

    protected TimedEffect(long duration) {
        this.endTime = System.currentTimeMillis() + duration;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }
}
