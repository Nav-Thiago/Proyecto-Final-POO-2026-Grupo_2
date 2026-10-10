package model;

public class MeleeWeapon extends Weapon {

    private final double range = 100.0; // Píxeles de alcance del cuchillo
    private final double arcAngle = Math.toRadians(90); // 90 grados de arco de ataque

    public MeleeWeapon(int damage, long cooldown) {
        super(damage);
        this.cooldown = cooldown;
    }

    @Override
    public boolean attack(Player player, GameState state) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - this.lastShoot >= this.cooldown) {
            this.lastShoot = currentTime;

            double totalDamage = this.getDamage() + player.getDamageBonus();
            boolean hitSomething = false;

            for (Enemy zombie : state.getEnemies()) {
                if (!zombie.isAlive()) continue;

                double playerCenterX = player.getX() + (player.getWidth() / 2.0);
                double playerCenterY = player.getY() + (player.getHeight() / 2.0);

                double zombieCenterX = zombie.getX() + (zombie.getWidth() / 2.0);
                double zombieCenterY = zombie.getY() + (zombie.getHeight() / 2.0);

                double dx = zombieCenterX - playerCenterX;
                double dy = zombieCenterY - playerCenterY;

                double distance = Math.hypot(dx, dy);

                if (distance <= range) {
                    double angleToZombie = Math.atan2(dy, dx); // Calculo el ángulo hacia el zombie

                    double angleDiff = angleToZombie - player.getAngle();
                    angleDiff = Math.atan2(Math.sin(angleDiff), Math.cos(angleDiff));

                    if (Math.abs(angleDiff) <= this.arcAngle / 2.0) {
                        zombie.takeDamage((int) totalDamage);
                        hitSomething = true;
                    }
                }
            }
            return hitSomething;
        }
        return false;
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }
}
