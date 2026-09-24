package model;

import java.awt.Color;

public class MaxAmmo extends PowerUp {

    public MaxAmmo(double x, double y) {
        super(x, y);
    }

    @Override
    protected void apply(Player player) {
        player.refillAmmo();
    }

    @Override
    public String getLabel() { return "MAX"; }

    @Override
    public Color getColor() { return Color.ORANGE; }
}
