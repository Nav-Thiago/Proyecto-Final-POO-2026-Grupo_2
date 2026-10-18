package model;

public class Ammo extends PowerUp {

    public Ammo(double x, double y) {
        super(x, y);
    }

    @Override
    protected void apply(Player player) {
        player.addPickupAmmo();
    }
}