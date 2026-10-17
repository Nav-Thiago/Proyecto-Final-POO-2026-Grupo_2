package model;

public abstract class Enemy extends Character {

    private final int ATTACK_COOLDOWN= 1000;
    protected double angle;
    private long lastAttackTime = 0;
    private static final double[] AVOID_ANGLES_DEG = {0, 25, -25, 50, -50, 75, -75, 100, -100};
    private boolean isCollidingWithPlayer = false;
    private long collisionStartTime = 0;
    private final int ATTACK_WINDUP = 400;

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

    public boolean isCollidingWithPlayer() {
        return isCollidingWithPlayer;
    }

    public void setCollidingWithPlayer(boolean colliding) {
        this.isCollidingWithPlayer = colliding;
    }

    public void setCollisionStartTime(long time) {
        this.collisionStartTime = time;
    }

    public boolean hasCompletedAttackDelay() {
        return (System.currentTimeMillis() - collisionStartTime >= ATTACK_WINDUP);
    }

    public void update(Player target, GameState state) {
        // Verifica si el zombie está muerto antes de moverse
        if (!isAlive()) return;

        double playerCenterX = target.getX() + (target.getWidth() / 2.0);
        double playerCenterY = target.getY() + (target.getHeight() / 2.0);
        double zombieCenterX = this.x + (this.width / 2.0);
        double zombieCenterY = this.y + (this.height / 2.0);

        double dx = playerCenterX - zombieCenterX;
        double dy = playerCenterY - zombieCenterY;

        double distance = Math.hypot(dx, dy);
        this.angle = Math.atan2(dy, dx);

        if (distance > 1000) {
            return;
        }

        if (distance > 30) {
            moveTowardsWithAvoidance(dx, dy, state);
        }

        avoidOverlap(state);
    }

    private void moveTowardsWithAvoidance(double dx, double dy, GameState state) {
        double baseAngle = Math.atan2(dy, dx);
        double length = Math.hypot(dx, dy);

        if (length == 0) return;

        for (double offsetDeg : AVOID_ANGLES_DEG) {
            double testAngle = baseAngle + Math.toRadians(offsetDeg);
            double testDx = Math.cos(testAngle) * length;
            double testDy = Math.sin(testAngle) * length;

            if (canMove(testDx, testDy, state)) {
                move(testDx, testDy, state);
                return;
            }
        }
    }

    public void avoidOverlap(GameState state) {
        for (Enemy otherZombie : state.getEnemies()) {
            if (this == otherZombie || !otherZombie.isAlive()) continue;

            if (this.collidesWith(otherZombie)) {

                double dx = this.getX() - otherZombie.getX();
                double dy = this.getY() - otherZombie.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance > 0) {
                    double pushX = (dx/distance) * 1.5;
                    double pushY = (dy/distance) * 1.5;

                    if (!isCollidingWithMap(x + pushX, y, state)) {
                        x += pushX;
                    }

                    if (!isCollidingWithMap(x, y + pushY, state)) {
                        y += pushY;
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

    public boolean isAttacking() {
        return (System.currentTimeMillis() - lastAttackTime < 500);
    }
}
