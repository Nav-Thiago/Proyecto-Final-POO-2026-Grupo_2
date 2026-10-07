package model;

public class CollisionSystem {

    public void update (GameState state) {
        checkBulletCollision(state);
        checkPoisonPlayerCollision(state);
        checkZombiePlayerCollision(state);
    }

    private void checkBulletCollision (GameState state) {
        for (Bullet bullet : state.getPlayerBullets()) {
            if (!bullet.isActive()) continue;

            for (Enemy zombie : state.getEnemies()) {
                if (zombie.isAlive() && bullet.collidesWith(zombie)) {
                    bullet.setActive(false);
                    zombie.takeDamage((int) bullet.getDamage());
                    break;
                }
            }
        }
    }

    private void checkPoisonPlayerCollision (GameState state) {
        Player player = state.getPlayer();
        if (!player.isAlive()) return;

        for (PoisonBullet poison : state.getPoisonBullets()) {
            if (poison.isActive() && poison.collidesWith(player)) {
                poison.setActive(false);
                poison.applyTo(player);//POLIMORFISMO: cada bala sabe que hacer
            }
        }
    }

    private void checkZombiePlayerCollision(GameState state) {
        Player player = state.getPlayer();
        if (!player.isAlive()) return;

        for (Enemy zombie : state.getEnemies()) {
            if (!zombie.isAlive()) continue;

            if (zombie.collidesWith(player)) {

                if (zombie.canAttack()) {
                    player.takeDamage(zombie.attack());
                    zombie.resetAttackCooldown();
                }
            }
        }
    }
}
