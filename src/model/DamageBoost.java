package model;
 
import java.awt.Color;
 
public class DamageBoost extends PowerUp {
 
    private static final double BONUS = 25; // +25 de daño por bala (25 -> 50)
    private final long duration;                   // en milisegundos
 
    public DamageBoost(double x, double y, long duration) {
        super(x, y);
        this.duration = duration;
    }
 
    @Override
    protected void apply(Player player) {
        player.addEffect(new DamageBoostEffect(BONUS, duration));
    }
 
    @Override
    public String getLabel() { return "+25"; }
 
    @Override
    public Color getColor() { return Color.RED; }
}
