package model;
 
public class DamageBoostEffect extends TimedEffect {
 
    private final double bonus;
 
    public DamageBoostEffect(double bonus, long duration) {
        super(duration);
        this.bonus = bonus;
    }
 
    @Override
    public void apply(Player player) {
        player.setDamageBonus(player.getDamageBonus() + bonus);
    }
 
    @Override
    public void remove(Player player) {
        player.setDamageBonus(player.getDamageBonus() - bonus);
    }
}
 