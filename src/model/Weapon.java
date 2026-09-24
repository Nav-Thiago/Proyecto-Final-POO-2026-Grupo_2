package model;

import java.util.List;

public abstract class Weapon {

    protected long cooldown;
    protected long lastShoot = 0;
    private int damage;

    public Weapon(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public abstract List<Bullet> shoot(double x, double y, double angle);

}
