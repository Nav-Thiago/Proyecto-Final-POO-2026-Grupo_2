package model;

public class PoisonZombie extends RangedEnemy {

    private static final double SHOOT_RANGE = 300;
    private static final long SHOOT_COOLDOWN = 1500;

    private long lastShootTime;

    public PoisonZombie(double x, double y, GameClock clock) {
        super(x, y, EnemyType.POISON, 40, 64, 2.0, 100, 5);
        this.lastShootTime = clock.nowMillis();
    }

    @Override
    protected PoisonBullet createProjectile(double x, double y, double dirX, double dirY) {
        return new PoisonBullet(x, y, 6, 6, dirX, dirY, 10.0);
    }

    @Override
    public void update(Player player, GameState state) {
        if (!isAlive()) return;

        double distance = faceTarget(player); // siempre mira al jugador, también cuando dispara

        if (distance > SHOOT_RANGE) {
            super.update(player, state); // se acerca (reutiliza persecución y evasión de Enemy)
        } else {
            long currentTime = state.getClock().nowMillis();
            if (currentTime - lastShootTime >= SHOOT_COOLDOWN) {
                shootAt(player, state);
                lastShootTime = currentTime;
            }
            avoidOverlap(state);
        }
    }
}