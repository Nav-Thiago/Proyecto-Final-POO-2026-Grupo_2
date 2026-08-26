package model;

public abstract class Character extends Entity {

    protected double speed;
    private int health;
    private int maxHealth;

    public Character(double x, double y, int width, int height, double speed, int health, int maxHealth) {
        super(x, y, width, height);
        this.speed = speed;
        this.maxHealth = maxHealth;
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    protected void move(double dx, double dy) {
        if (dx != 0 || dy != 0) {
            double length = Math.sqrt(dx * dx + dy * dy);
            x += (dx/length * speed);
            y += (dy/length * speed);
        }
    }

    public void takeDamage(int amount) {
        if (amount < this.health) {
            health -= amount;
        } else {
            this.health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }
}
