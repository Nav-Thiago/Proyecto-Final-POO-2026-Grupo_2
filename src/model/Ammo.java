package model;

public class Ammo extends PowerUp {

    private final int amount;

    public Ammo(double x, double y, int amount) {
        super(x, y);
        this.amount = amount;
    }

    @Override
    protected void apply(Player player) {
        player.addAmmo(amount);
    }
}
