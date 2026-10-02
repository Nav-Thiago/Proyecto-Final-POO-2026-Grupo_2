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

    public abstract void attack(double x, double y, double angle, List<Bullet> bullets, List<Enemy> enemies);
}
