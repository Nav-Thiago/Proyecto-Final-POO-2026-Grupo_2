package model;

import java.util.List;

public class MeleeWeapon extends Weapon {

    public MeleeWeapon(int damage, long cooldown) {
        super(damage);
        this.cooldown = cooldown;
    }

    @Override
    public List<Bullet> shoot(double x, double y, double angle) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - this.lastShoot >= this.cooldown) {
            this.lastShoot = currentTime;

            double dirX = Math.cos(angle);
            double dirY = Math.sin(angle);

            return List.of(new MeleeHitbox(x, y, dirX, dirY, this.getDamage()));
        }

        return List.of();
    }
}
