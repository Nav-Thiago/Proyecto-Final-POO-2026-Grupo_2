package model;

public abstract class Enemy extends Character {

    private static final int ATTACK_COOLDOWN = 600;  // pausa entre golpes (600 + 400 de windup = ~1000 ms por ciclo)
    private static final int ATTACK_WINDUP = 400;    // instante del golpe dentro de la animación
    private static final int ATTACK_ANIM_MS = 700;   // duración total de la animación (windup + recuperación)

    private static final double[] AVOID_ANGLES_DEG = {0, 25, -25, 50, -50, 75, -75, 100, -100};

    protected double angle;
    private long lastAttackTime = 0;
    private long attackStart = -1;   // -1 = no está atacando
    private boolean damageDealt;

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


    public boolean canAttack() {
        return System.currentTimeMillis() - lastAttackTime >= ATTACK_COOLDOWN;
    }

    public void resetAttackCooldown() {
        this.lastAttackTime = System.currentTimeMillis();
    }

    public void tryAttack(Player player) {
        long now = System.currentTimeMillis();

        // Arranca un ataque nuevo si no hay uno en curso y el cooldown está listo
        if (attackStart < 0) {
            if (!canAttack()) return;
            attackStart = now;
            damageDealt = false;
        }

        // El daño cae al final del windup, cuando la animación llega al golpe
        if (!damageDealt && now - attackStart >= ATTACK_WINDUP) {
            player.takeDamage(attack());
            resetAttackCooldown();
            damageDealt = true;
        }

        // Terminó la animación completa
        if (now - attackStart >= ATTACK_ANIM_MS) {
            attackStart = -1;
        }
    }

    public void cancelWindup() {
        if (attackStart < 0) return;

        long elapsed = System.currentTimeMillis() - attackStart;
        if (!damageDealt || elapsed >= ATTACK_ANIM_MS) {
            attackStart = -1;
        }
    }

    public double getAttackProgress() {
        if (attackStart < 0) return -1;

        long elapsed = System.currentTimeMillis() - attackStart;
        if (elapsed >= ATTACK_ANIM_MS) return -1;

        return elapsed / (double) ATTACK_ANIM_MS;
    }

    public double getSpitProgress() {
        return -1;
    }

    public abstract int attack();
}
