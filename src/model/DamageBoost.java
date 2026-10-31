package model;
 
public class DamageBoost extends PowerUp {
 
    private static final double BONUS = 25; // +25 de daño por bala (25 -> 50)
    private final long duration; // en milisegundos
 
    public DamageBoost(double x, double y, long duration) {
        super(x, y);
        this.duration = duration;
    }
 
    @Override
    protected void apply(Player player) {
        player.decorateStats(s -> new DamageBoostDecorator(s, BONUS, duration, player.getClock()));
    }
}