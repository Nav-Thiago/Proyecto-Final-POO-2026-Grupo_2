package model;

public class MeleeHitbox extends Bullet {

    private int framesAlive = 0;
    private static final int MAX_FRAMES = 5;

    public MeleeHitbox(double x, double y, double dirX, double dirY, double damage) {
        // Posiciono el área de impacto 30 píxeles por delante del centro del jugador
        super (x + (dirX * 30), y + (dirY * 30), 0, 0, 0, damage);

        // El área de impacto es más grande que una bala normal
        this.width = 40;
        this.height = 40;
    }

    @Override
    public void update(int mapWidth, int mapHeight) {
        this.framesAlive++;
        if (framesAlive >= MAX_FRAMES) {
            this.setActive(false); // El GamePanel lo elimina
        }
    }
}
