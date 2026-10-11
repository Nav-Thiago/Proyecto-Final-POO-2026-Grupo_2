package model;

/**
 * COMPONENTE del patrón Decorator.
 * Define todo lo que los power ups pueden modificar del jugador en combate.
 * El Player solo conoce esta interfaz, no sabe qué power ups hay activos.
 */
public interface PlayerStats {

    /** Daño extra que se suma a cada ataque. */
    double getDamageBonus();

    /** Puntos de escudo disponibles en total. */
    int getShield();

    /** Recibe un daño y devuelve cuánto de ese daño NO fue absorbido (el que llega a la vida). */
    int absorbDamage(int amount);

    /** true si el decorador ya cumplió su función y se puede descartar. */
    boolean isExpired();

    /** Devuelve la cadena sin los decoradores vencidos. */
    PlayerStats prune();

    /** Se llama una sola vez, cuando el decorador se agrega al jugador. */
    default void onApply(Player player) {
    }
}
