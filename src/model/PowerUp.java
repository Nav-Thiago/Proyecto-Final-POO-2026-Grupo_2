package model;

/**
 Clase padre de todos los power ups.
 Extiende Entity, reutiliza posición, tamaño y collidesWith().
 */
public abstract class PowerUp extends Entity {

    public static final int SIZE = 32;

    private boolean active = true;

    public PowerUp(double x, double y) {
        super(x, y, SIZE, SIZE);
    }

    public boolean isActive() {
        return active;
    }

    // Template method: el juego lo llama al tocarlo. Cada hija solo define apply().
    public final void collect(Player player) {
        if (!active) return;
        apply(player);
          player.notifyPickup(getClass().getSimpleName());
        active = false;
    }

    // Efecto concreto de cada power up sobre el jugador.
    protected abstract void apply(Player player);
}
