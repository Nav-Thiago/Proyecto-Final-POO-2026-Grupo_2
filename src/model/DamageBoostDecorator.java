package model;

// Suma daño extra a cada ataque durante un tiempo limitado.
public class DamageBoostDecorator extends StatsDecorator {

    private final double bonus;
    private final long endTime;
    private final GameClock clock;

    public DamageBoostDecorator(PlayerStats wrapped, double bonus, long durationMs, GameClock clock) {
        super(wrapped);
        this.bonus = bonus;
        this.clock = clock;
        this.endTime = clock.nowMillis() + durationMs;
    }

    @Override
    public double getDamageBonus() {
        return wrapped.getDamageBonus() + bonus; // se acumula con otros boosts activos
    }

    @Override
    public boolean isExpired() {
        return clock.nowMillis() > endTime;
    }
}
