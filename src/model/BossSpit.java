package model;

public class BossSpit extends PoisonBullet {

    private static final int SIZE = 40;   // tamaño del veneno(proyectil) del boss

    public BossSpit(double x, double y, double dirX, double dirY, double speed, double damage) {
        // x, y llegan como el centro de salida; se corre media bala para que salga centrada
        super(x - SIZE / 2.0, y - SIZE / 2.0, SIZE, SIZE, dirX, dirY, speed, damage);
    }

    @Override
    public void applyTo(Player player) {
        player.takeDamage((int) getDamage());
    }
}