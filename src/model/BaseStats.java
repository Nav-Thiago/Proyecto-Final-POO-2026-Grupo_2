package model;

/** COMPONENTE CONCRETO: el jugador sin ningún power up activo. */
public class BaseStats implements PlayerStats {

    @Override
    public double getDamageBonus() {
        return 0;
    }

    @Override
    public int getShield() {
        return 0;
    }

    @Override
    public int absorbDamage(int amount) {
        return amount; // no absorbe nada: todo el daño pasa a la vida
    }

    @Override
    public boolean isExpired() {
        return false; // la base nunca se descarta
    }

    @Override
    public PlayerStats prune() {
        return this;
    }
}
