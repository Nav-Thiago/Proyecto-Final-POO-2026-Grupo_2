package model;

public abstract class Enemy extends Character {

    private static final long ATTACK_COOLDOWN = 800; // Constante de tiempo de espera

    private long lastAttack = 0; // Lo incializo en 0 para que pueda atacar inmediatamente la primera vez

    public Enemy(double x, double y, int width, int height, double speed, int health, int maxHealth) {
        super(x, y, width, height, speed, health, maxHealth);
    }

    public void update(Player target) {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        move(dx, dy);

        long currentTime = System.currentTimeMillis();
        if (getBounds().intersects(target.getBounds()) && currentTime - lastAttack >= ATTACK_COOLDOWN) {
            target.takeDamage(attack());
            lastAttack = currentTime;
        }
    }

    public abstract int attack();
}
