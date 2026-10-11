package model;

/** Agrega puntos de escudo: absorbe daño antes de que llegue a la vida. Se descarta al agotarse. */
public class ShieldDecorator extends StatsDecorator {

    public static final int MAX_SHIELD = 100; // tope del escudo total del jugador

    private int points;

    public ShieldDecorator(PlayerStats wrapped, int points) {
        super(wrapped);
        // Respeta el tope: lo que ya hay en la cadena cuenta para el máximo
        int room = Math.max(0, MAX_SHIELD - wrapped.getShield());
        this.points = Math.max(0, Math.min(points, room));
    }

    @Override
    public int getShield() {
        return points + wrapped.getShield();
    }

    @Override
    public int absorbDamage(int amount) {
        int absorbed = Math.min(points, amount);
        points -= absorbed;
        // lo que no absorbí pasa al siguiente decorador de la cadena
        return wrapped.absorbDamage(amount - absorbed);
    }

    @Override
    public boolean isExpired() {
        return points <= 0;
    }
}
