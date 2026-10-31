package model;

// Efecto que se aplica sobre el jugador
public interface Effect {
    boolean isExpired();
    void update (Player player);
}
