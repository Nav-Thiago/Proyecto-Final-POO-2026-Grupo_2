package model;

import java.awt.Color;

public class Heal extends PowerUp {

    private final int amount;

    public Heal(double x, double y, int amount) {
        super(x, y);
        this.amount = amount;
    }

    @Override
    protected void apply(Player player) {
        player.heal(amount);
    }

    @Override
    public String getLabel() { return "HP"; }

    @Override
    public Color getColor() { return Color.GREEN; }
}
