package model;

public abstract class Enemy extends Character {

    private final int ATTACK_COOLDOWN= 1000;
    protected double angle;
    private long lastAttackTime = 0;

    public Enemy(double x, double y, int width, int height, double speed, int health, int maxHealth) {
        super(x, y, width, height, speed, health, maxHealth);
        this.angle = 0.0;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    public double getAngle() {
        return angle;
    }

    public void update(Player target, GameState state) {
        // Verifica si el zombie está muerto antes de moverse
        if (!isAlive()) return;

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        this.angle = Math.atan2(dy, dx);

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 30) {
            move(dx, dy, state);
        }

        avoidOverlap(state);
    }

    public void avoidOverlap(GameState state) {
        for (Enemy otherZombie : state.getEnemies()) {
            if (this != otherZombie && otherZombie.isAlive()) {

                if (this.collidesWith(otherZombie)) {

                    double dx = this.getX() - otherZombie.getX();
                    double dy = this.getY() - otherZombie.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance > 0) {
                        this.x += (dx/distance) * 1.5;
                        this.y += (dy/distance) * 1.5;
                    }
                }
            }
        }
    }

    public boolean canAttack() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAttackTime >= ATTACK_COOLDOWN);
    }

    public void resetAttackCooldown() {
        this.lastAttackTime = System.currentTimeMillis();
    }

    public abstract int attack();
}
