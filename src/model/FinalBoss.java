package model;

import java.util.List;

public class FinalBoss extends RangedEnemy{

    private static final long SPIT_COOLDOWN = 5000;
    private static final int SPIT_DAMAGE = 50;

    private long lastSpitTime;

    public FinalBoss(double x, double y, List<PoisonBullet> poisonList) {
        super(x, y, 110, 110, 3.5, 500, 500, poisonList);
        this.lastSpitTime = System.currentTimeMillis();
    }

    @Override
    public int attack() {
        return 30; // mismo daño que TankZombie
    }

    @Override
    protected PoisonBullet createProjectile(double x, double y, double dirX, double dirY) {
        return new BossSpit(x, y, dirX, dirY, SPIT_DAMAGE);
    }

    @Override
    public void update(Player player, GameState state) {
        if (!isAlive()) return;

        super.update(player, state); // persigue al jugador

        long now = System.currentTimeMillis();
        if (now - lastSpitTime >= SPIT_COOLDOWN) {
            shootAt(player);
            resetAttackCooldown(); 
            lastSpitTime = now;
        }
    }

    @Override
    public boolean isSpitting() {
        return (System.currentTimeMillis() - lastSpitTime < 500);
    }
}