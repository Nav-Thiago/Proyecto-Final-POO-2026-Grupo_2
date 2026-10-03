package model;

public class PoisonBullet extends Bullet {

    public PoisonBullet(double x, double y, double dirX, double dirY, double speed, double damage) {
        super(x, y, dirX, dirY, speed, damage);
    }

    /** Qué le hace esta bala al jugador cuando lo toca. */
    public void applyTo(Player player) {
        player.addEffect(new PoisonEffect(4000));
    }
}
