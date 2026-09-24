package model;

import java.util.List;

public class MeleeWeapon extends Weapon {

    public MeleeWeapon(int damage, long cooldown) {
        super(damage);
        this.cooldown = cooldown;
    }

    @Override
    public List<Bullet> shoot(double x, double y, double angle) {
        return List.of();
    }
}
