package model;

/** Suma daño extra a cada ataque durante un tiempo limitado. */
public class DamageBoostDecorator extends StatsDecorator {

    private final double bonus;
    private final long endTime;

    public DamageBoostDecorator(PlayerStats wrapped, double bonus, long durationMs) {
        super(wrapped);
        this.bonus = bonus;
        this.endTime = System.currentTimeMillis() + durationMs;
    }

    @Override
    public double getDamageBonus() {
        return wrapped.getDamageBonus() + bonus; // se acumula con otros boosts activos
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }
}
