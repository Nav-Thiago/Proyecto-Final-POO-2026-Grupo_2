package model;

/*
  decorador base, implementa PlayerStats y envuelve a otro PlayerStats.
  Por defecto delega todo en el objeto envuelto, cada decorador concreto
  redefine solo lo que le interesa
 */
public abstract class StatsDecorator implements PlayerStats {

    protected PlayerStats wrapped;

    protected StatsDecorator(PlayerStats wrapped) { //wrapped = envuelto, referencia al objeto que se está decorando
        this.wrapped = wrapped;
    }

    @Override
    public double getDamageBonus() {
        return wrapped.getDamageBonus();
    }

    @Override
    public int getShield() {
        return wrapped.getShield();
    }

    @Override
    public int absorbDamage(int amount) {
        return wrapped.absorbDamage(amount);
    }

    // Limpia primero lo de adentro y después se quita a sí mismo si venció
    @Override
    public PlayerStats prune() {
        wrapped = wrapped.prune();
        return isExpired() ? wrapped : this;
    }
}
