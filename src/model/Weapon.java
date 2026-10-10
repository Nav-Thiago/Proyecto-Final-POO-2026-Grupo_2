package model;
 
public abstract class Weapon {
 
    // Boca del arma respecto al centro del jugador (px). Las balas y el destello salen de acá.
    // Si no coincide con la punta del sprite, se ajusta SOLO en estas dos constantes.
    public static final double MUZZLE_FORWARD = 40;
    public static final double MUZZLE_SIDE = 17;
 
    protected long cooldown;
    protected long lastShoot = 0;
    private int damage;
 
    public Weapon(int damage) {
        this.damage = damage;
    }
 
    public int getDamage() {
        return damage;
    }
 
    /** Momento (ms) del último disparo; la vista lo usa para dibujar el destello. */
    public long getLastShoot() {
        return lastShoot;
    }
 
    /** Posición X de la boca del arma en el mundo, según hacia dónde apunta el jugador. */
    protected double getMuzzleX(Player player) {
        double centerX = player.getX() + player.getWidth() / 2.0;
        double a = player.getAngle();
        return centerX + Math.cos(a) * MUZZLE_FORWARD - Math.sin(a) * MUZZLE_SIDE;
    }
 
    /** Posición Y de la boca del arma en el mundo. */
    protected double getMuzzleY(Player player) {
        double centerY = player.getY() + player.getHeight() / 2.0;
        double a = player.getAngle();
        return centerY + Math.sin(a) * MUZZLE_FORWARD + Math.cos(a) * MUZZLE_SIDE;
    }
 
    public abstract boolean attack(Player player, GameState state);

    public abstract boolean isAutomatic();
}