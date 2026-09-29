package model;

/** Efecto que se aplica sobre el jugador y puede vencer con el tiempo. */
public interface Effect {
    void apply(Player player);
    void remove(Player player);
    boolean isExpired();

    default void update (Player player){}
}
