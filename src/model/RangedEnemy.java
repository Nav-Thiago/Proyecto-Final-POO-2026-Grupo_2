package model;

public abstract class RangedEnemy extends Enemy {

    public RangedEnemy(double x, double y, EnemyType type, int width, int height, double speed, int health, int attackDamage) {
        super(x, y, type, width, height, speed, health, attackDamage);
    }

    //Cada hija decide qué proyectil crea. 
    protected abstract PoisonBullet createProjectile(double x, double y, double dirX, double dirY);

    // Código común: apunta al jugador y dispara. 
    protected void shootAt(Player target, GameState state) {
        double startX = getX() + getWidth() / 2.0;
        double startY = getY() + getHeight() / 2.0;

        double dx = (target.getX() + target.getWidth() / 2.0) - startX;
        double dy = (target.getY() + target.getHeight() / 2.0) - startY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            state.addPoisonBullet(createProjectile(startX, startY, dx / distance, dy / distance));
        }
    }
}