package model;

public abstract class Enemy extends Character {

    private static final long ATTACK_COOLDOWN = 800; // Constante de tiempo de espera

    private long lastAttack = 0; // Lo incializo en 0 para que pueda atacar inmediatamente la primera vez

    protected double angulo; 

    public Enemy(double x, double y, int width, int height, double speed, int health, int maxHealth) {
        super(x, y, width, height, speed, health, maxHealth);
        this.angulo = 0.0;
    }

    public double getAngulo() {
        return angulo;
    }

    public void update(Player target) {
        // Verifica si el zombie está muerto antes de moverse
        if (!isAlive()) {
            return;
        }

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        this.angulo = Math.atan2(dy, dx);

        move(dx, dy);

        long currentTime = System.currentTimeMillis();
        if (getBounds().intersects(target.getBounds()) && currentTime - lastAttack >= ATTACK_COOLDOWN) {
            target.takeDamage(attack());
            lastAttack = currentTime;
        }
    }

    public abstract int attack();
}
