package model;

public class PoisonEffect implements Effect {

    private static final int TICK_INTERVAL = 1000;
    private static final int DAMAGE_PER_TICK = 5;

    private final GameClock clock;
    private final long endTime;
    private long lastTickTime;

    public PoisonEffect(long duration, GameClock clock){
        this.clock = clock;
        this.endTime = clock.nowMillis() + duration;
        this.lastTickTime = clock.nowMillis();
    }

    @Override 
    public void update (Player player){
        long currentTime = clock.nowMillis();

        if (currentTime - lastTickTime >= TICK_INTERVAL) {
            player.takeDamage(DAMAGE_PER_TICK);
            lastTickTime = currentTime;
        }
    }

    @Override
    public boolean isExpired() {
        return clock.nowMillis() > endTime;
    }
}
