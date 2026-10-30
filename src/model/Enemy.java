package model;

public abstract class Enemy extends Character {

    private static final double[] AVOID_ANGLES_DEG = {0, 25, -25, 50, -50, 75, -75, 100, -100};

    protected double angle;
    private long lastAttackTime = 0;
    private long attackStart = -1;   // -1 = no está atacando
    private boolean damageDealt;

    public Enemy(double x, double y, int width, int height, double speed, int health, int maxHealth) {
        super(x, y, width, height, speed, health, maxHealth);
        this.angle = 0.0;
    }

    public double getAngle() {
        return angle;
    }


    protected int getAttackCooldown() {
        return 600;
    }

    protected int getAttackWindup() {
        return 400;
    }

    protected int getAttackAnimMs() {
        return 700;
    }

    protected double getSightRange() {
        return 1000;
    }

    protected double getStopDistance() {
        return 30;
    }

    public void update(Player target, GameState state) {
        // Verifica si el zombie está muerto antes de moverse
        if (!isAlive()) return;

        double dx = dxTo(target);
        double dy = dyTo(target);
        double distance = faceTarget(target);

        if (distance > getSightRange()) {
            return;
        }

        if (distance > getStopDistance()) {
            moveTowardsWithAvoidance(dx, dy, state);
        }

        avoidOverlap(state);
    }

    protected double faceTarget(Player target) {
        double dx = dxTo(target);
        double dy = dyTo(target);
        this.angle = Math.atan2(dy, dx);
        return Math.hypot(dx, dy);
    }

    private double dxTo(Player target) {
        return (target.getX() + target.getWidth() / 2.0) - (this.x + this.width / 2.0);
    }

    private double dyTo(Player target) {
        return (target.getY() + target.getHeight() / 2.0) - (this.y + this.height / 2.0);
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
                    double pushX = (dx / distance) * 1.5;
                    double pushY = (dy / distance) * 1.5;

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

    public boolean canAttack(long now) {
        return now - lastAttackTime >= getAttackCooldown();
    }

    public void resetAttackCooldown(long now) {
        this.lastAttackTime = now;
    }

    public void tryAttack(Player player) {
        long now = player.getClock().nowMillis();

        // Arranca un ataque nuevo si no hay uno en curso y el cooldown está listo
        if (attackStart < 0) {
            if (!canAttack(now)) return;
            attackStart = now;
            damageDealt = false;
        }

        // El daño cae al final del windup, cuando la animación llega al golpe
        if (!damageDealt && now - attackStart >= getAttackWindup()) {
            player.takeDamage(attack());
            resetAttackCooldown(now);
            damageDealt = true;
        }

        // Terminó la animación completa
        if (now - attackStart >= getAttackAnimMs()) {
            attackStart = -1;
        }
    }

    public void cancelWindup(long now) {
        if (attackStart < 0) return;

        long elapsed = now - attackStart;
        if (!damageDealt || elapsed >= getAttackAnimMs()) {
            attackStart = -1;
        }
    }

    public double getAttackProgress(long now) {
        if (attackStart < 0) return -1;

        long elapsed = now - attackStart;
        if (elapsed >= getAttackAnimMs()) return -1;

        return elapsed / (double) getAttackAnimMs();
    }

    public double getSpitProgress(long now) {
        return -1;
    }

    public abstract int attack();
}
