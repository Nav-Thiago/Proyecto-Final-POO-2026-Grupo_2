package model;

public class PoisonBullet extends Bullet {

    // Tamaño personalizado, lo usa BossSpit
    public PoisonBullet(double x, double y, int width, int height, double dirX, double dirY, double damage) {
        super(x, y, width, height, dirX, dirY, damage, 40);
    }

    /** Qué le hace esta bala al jugador cuando lo toca. */
    public void applyTo(Player player) {
        player.addEffect(new PoisonEffect(4000, player.getClock()));
    }
}
