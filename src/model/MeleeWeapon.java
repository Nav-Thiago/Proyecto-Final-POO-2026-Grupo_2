package model;

import java.util.List;

public class MeleeWeapon extends Weapon {

    public MeleeWeapon(int damage, long cooldown) {
        super(damage);
        this.cooldown = cooldown;
    }

    public void attack(double x, double y, double angle, List<Bullet> bullets, List<Enemy> enemies) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - this.lastShoot >= this.cooldown) {
            this.lastShoot = currentTime;

            double range = 70.0;

            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    double enemyCenterX = enemy.getX() + (enemy.getWidth() / 2.0);
                    double enemyCenterY = enemy.getY() + (enemy.getHeight() / 2.0);

                    double distance = Math.hypot(enemyCenterX - x, enemyCenterY - y);
                    if (distance <= range) {
                        enemy.takeDamage(this.getDamage());
                        break;
                    }
                }
            }
        }
    }
}
