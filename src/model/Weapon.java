package model;

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

    public abstract boolean attack(Player player, GameState state);
}
