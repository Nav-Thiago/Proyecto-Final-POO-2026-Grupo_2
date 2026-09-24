package model;

import java.awt.Color;

public class Shield extends PowerUp {

    private final int shieldPoints;

    public Shield(double x, double y, int shieldPoints) {
        super(x, y);
        this.shieldPoints = shieldPoints;
    }

    @Override
    protected void apply(Player player) {
        player.addShield(shieldPoints);
    }

    @Override
    public String getLabel() { return "SH"; }

    @Override
    public Color getColor() { return Color.CYAN; }
}
