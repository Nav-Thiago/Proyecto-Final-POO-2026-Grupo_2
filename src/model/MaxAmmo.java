package model;

public class MaxAmmo extends PowerUp {

    public MaxAmmo(double x, double y) {
        super(x, y);
    }

    @Override
    protected void apply(Player player) {
        player.decorateStats(MaxAmmoDecorator::new);
    }
}
