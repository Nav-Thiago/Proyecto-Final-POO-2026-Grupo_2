package model;

public abstract class Enemy extends Character {

    protected double angle;
    private long lastAttackTime = 0;
    private final int meeleCooldown = 1000; 

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

    public void update(Player target) {
        // Verifica si el zombie está muerto antes de moverse
        if (!isAlive()) {
            return;
        }

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        this.angle = Math.atan2(dy, dx);

        move(dx, dy);

        long currentTime = System.currentTimeMillis();
        if (this.getBounds().intersects(target.getBounds()) && currentTime - lastAttackTime >= meeleCooldown) {
            target.takeDamage(attack());
            lastAttackTime = currentTime; 
        }
    }

    public abstract int attack();
}
