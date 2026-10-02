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

        for (Bullet poison : state.getPoisonBullets()) {
            if (poison.isActive() && poison.collidesWith(player)) {
                poison.setActive(false);
                player.addEffect(new PoisonEffect(4000));
            }
        }
    }

    private void checkZombiePlayerCollision(GameState state) {

    }

}
