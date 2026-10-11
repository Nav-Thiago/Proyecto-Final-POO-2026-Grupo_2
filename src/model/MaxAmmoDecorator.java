package model;

/**
 * Recarga al máximo la munición del arma equipada.
 * Es un efecto instantáneo: actúa al aplicarse (onApply) y se descarta en el próximo tick.
 */
public class MaxAmmoDecorator extends StatsDecorator {

    public MaxAmmoDecorator(PlayerStats wrapped) {
        super(wrapped);
    }

    @Override
    public void onApply(Player player) {
        player.refillAmmo();
    }

    @Override
    public boolean isExpired() {
        return true; // no queda activo: ya hizo su trabajo
    }
}
