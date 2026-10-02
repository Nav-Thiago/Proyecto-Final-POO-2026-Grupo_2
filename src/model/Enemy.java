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

    public void update(Player target, GameState state) {
        // Verifica si el zombie está muerto antes de moverse
        if (!isAlive()) return;

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

    public void applyRepulsion(GameState state) {
        for (Enemy otherZombie : state.getEnemies()) {
            if (this != otherZombie && otherZombie.isAlive()) {
                if (this.collidesWith(otherZombie)) {

                    double repX = this.getX() - otherZombie.getX();
                    double repY = this.getY() - otherZombie.getY();

                    if (repX == 0 && repY == 0) {
                        repX = Math.random() - 0.5;
                        repY = Math.random() - 0.5;
                    }

                    double distance = Math.sqrt(repX * repX + repY * repY);
                    double strength = 2.0;

                    this.setX(this.getX() + (repX / distance) * strength);
                    this.setY(this.getY() + (repY / distance) * strength);
                }
            }
        }
    }

    public abstract int attack();
}
