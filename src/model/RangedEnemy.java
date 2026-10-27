package model;

import java.util.List;

public abstract class RangedEnemy extends Enemy {

    private final List<PoisonBullet> projectiles;

    public RangedEnemy(double x, double y, int width, int height, double speed,
                       int health, int maxHealth, List<PoisonBullet> projectiles) {
        super(x, y, width, height, speed, health, maxHealth);
        this.projectiles = projectiles;
    }

    //Cada hija decide qué proyectil crea. 
    protected abstract PoisonBullet createProjectile(double x, double y, double dirX, double dirY);

    // Código común: apunta al jugador y dispara. 
    protected void shootAt(Player target) {
        double startX = getX() + getWidth() / 2.0;
        double startY = getY() + getHeight() / 2.0;

        double dx = (target.getX() + target.getWidth() / 2.0) - startX;
        double dy = (target.getY() + target.getHeight() / 2.0) - startY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            projectiles.add(createProjectile(startX, startY, dx / distance, dy / distance));
        }
    }
}