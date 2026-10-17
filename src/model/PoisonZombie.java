package model;

import java.util.List;

public class PoisonZombie extends RangedEnemy {

    private long lastShootTime;

    public PoisonZombie(double x, double y, List<PoisonBullet> poisonList) {
        super(x, y, 40, 64, 2.0, 80, 80, poisonList);
        this.lastShootTime = System.currentTimeMillis();
    }

    @Override
    public int attack() {
        return 5;
    }

    @Override
    protected PoisonBullet createProjectile(double x, double y, double dirX, double dirY) {
        return new PoisonBullet(x, y, 3, 3, dirX, dirY,10.0);
    }

    @Override
    public void update(Player player, GameState state) {
        double dx = player.getX() - this.getX();
        double dy = player.getY() - this.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 300) {
            super.update(player, state);
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime >= 1500) {
                shootAt(player);
                lastShootTime = currentTime;
            }
        }
    }
}