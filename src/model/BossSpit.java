package model;

public class BossSpit extends PoisonBullet {

    public BossSpit(double x, double y, double dirX, double dirY, double speed, double damage) {
        super(x, y, dirX, dirY, speed, damage);
    }

    @Override
    public void applyTo(Player player) {
        player.takeDamage((int) getDamage());
    }
}
