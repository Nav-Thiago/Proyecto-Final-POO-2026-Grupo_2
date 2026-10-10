package model;

import java.util.Map;
import java.util.Random;

public abstract class Enemy extends Character {

    private final int ATTACK_COOLDOWN= 1000;
    protected double angle;
    private long lastAttackTime = 0;
    private final Random random = new Random();
    private static final double[] AVOID_ANGLES_DEG = {0, 25, -25, 50, -50, 75, -75, 100, -100};
    // private static final long SIDESTEP_DURATION_MS = 450;
    // private int sidestepDirection = 0;
    // private long sidestepUntil = 0;


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

        double playerCenterX = target.getX() + (target.getWidth() / 2.0);
        double playerCenterY = target.getY() + (target.getHeight() / 2.0);
        double zombieCenterX = this.x + (this.width / 2.0);
        double zombieCenterY = this.y + (this.height / 2.0);

        double dx = playerCenterX - zombieCenterX;
        double dy = playerCenterY - zombieCenterY;

        double distance = Math.hypot(dx, dy);
        this.angle = Math.atan2(dy, dx);

        if (distance > 800) {
            return;
        }

        if (distance > 30) {
            double dirX = dx / distance;
            double dirY = dy / distance;

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

    /*
    private void moveWithSideStep(double dx, double dy, GameState state) {
        long now = System.currentTimeMillis();

        if (sidestepDirection != 0 && now < sidestepUntil) {

            if (trySideStep(dx, dy, state, sidestepDirection)) {
                return;
            }

            sidestepDirection *= -1;
            sidestepUntil = now + SIDESTEP_DURATION_MS;

            if (trySideStep(dx, dy, state, sidestepDirection)) {
                return;
            }

            sidestepDirection = 0;
            sidestepUntil = 0;
            return;
        }

        sidestepDirection = 0;
        sidestepUntil = 0;

        double oldX = x;
        double oldY = y;
        double distanceBefore = Math.hypot(dx, dy);

        move(dx, dy, state);

        double movedX = x - oldX;
        double movedY = y - oldY;

        double distanceAfter = Math.hypot(dx - movedX, dy - movedY);

        if (distanceAfter < distanceBefore - (this.speed * 0.5)) {
            return;
        }

        sidestepDirection = random.nextBoolean() ? 1 : -1;
        sidestepUntil = now + SIDESTEP_DURATION_MS;

        if (trySideStep(dx, dy, state, sidestepDirection)) {
            return;
        }

        sidestepDirection = 0;
        sidestepUntil = 0;
    }

    private boolean trySideStep (double dx, double dy, GameState state, int direction) {
        double sideX = -dy * direction;
        double sideY = dx * direction;

        double oldX = x;
        double oldY = y;

        move(sideX, sideY, state);

        double lateralMovement = Math.hypot(x - oldX, y - oldY);
        return lateralMovement > (this.speed * 0.5);
    }
     */

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

    private boolean hasMovedFrom(double oldX, double oldY) {
        return Math.abs(x - oldX) > 0.001 || Math.abs(y - oldY) > 0.001;
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
