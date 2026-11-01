package model;

import java.util.List;

public class FinalBoss extends RangedEnemy {

    private static final long SPIT_COOLDOWN = 5000;
    private static final long SPIT_ANIM_MS = 1000; // duración de la animación de escupir
    private static final int SPIT_DAMAGE = 50;

    private long lastSpitTime;
    private long spitAnimStart = -1; // -1 = no está escupiendo

    public FinalBoss(double x, double y, GameClock clock) {
        super(x, y, EnemyType.BOSS, 110, 110, 3.5, 1000, 1000, 30);
        this.lastSpitTime = clock.nowMillis();
    }

    @Override
    protected PoisonBullet createProjectile(double x, double y, double dirX, double dirY) {
        return new BossSpit(x, y, dirX, dirY, SPIT_DAMAGE);
    }

    @Override
    public void update(Player player, GameState state) {
        if (!isAlive()) return;

        super.update(player, state); // persigue al jugador

        long now = state.getClock().nowMillis();
        if (now - lastSpitTime >= SPIT_COOLDOWN) {
            shootAt(player, state);
            resetAttackCooldown(now);
            lastSpitTime = now;
            spitAnimStart = now; // arranca la animación de escupir
        }
    }

    @Override
    public double getSpitProgress(long now) {
        if (spitAnimStart < 0) return -1;

        long elapsed = now - spitAnimStart;
        if (elapsed >= SPIT_ANIM_MS) return -1;

        return elapsed / (double) SPIT_ANIM_MS;
    }
}