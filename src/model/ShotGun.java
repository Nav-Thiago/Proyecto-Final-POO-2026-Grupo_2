package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShotGun extends FireArm{

    private final Random random = new Random();

    public ShotGun(int damage, int cooldown, int actualMunition, int maxMunition) {
        super(damage, cooldown, actualMunition, maxMunition);
    }

    @Override
    public void attack(double x, double y, double angle, List<Bullet> pellets, List<Enemy> enemies) {
        long currentTime = System.currentTimeMillis();

        if (this.actualMunition > 0 && currentTime - this.lastShoot >= this.cooldown) {

            this.actualMunition -= 1;
            this.lastShoot = currentTime;

            for (int i = 0; i < 4; i++) {
                // random.nextDouble() da un valor entre 0.0 y 1.0.
                // Al restar 0.5 queda entre -0.5 y 0.5.
                // Multiplicado por 0.6 genera una dispersión máxima de +/- 0.3 radianes.
                double offset = (random.nextDouble() * 0.5) * 0.6;
                double spreadAngle = angle + offset;

                double dirX = Math.cos(spreadAngle);
                double dirY = Math.sin(spreadAngle);

                pellets.add(new Bullet(x, y, dirX, dirY, 12.0, this.getDamage()));
            }
        }
    }
}
