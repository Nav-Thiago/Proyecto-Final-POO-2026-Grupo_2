package model;

public class CollisionSystem {

    public void update (GameState state) {
        checkBulletZombieCollision(state);
        checkBulletMapCollision(state);
        checkPoisonPlayerCollision(state);
        checkZombiePlayerCollision(state);
    }

    private void checkBulletZombieCollision (GameState state) {
        for (Bullet bullet : state.getPlayerBullets()) {
            if (!bullet.isActive()) continue;

            for (Enemy zombie : state.getEnemies()) {
                if (zombie.isAlive() && bullet.collidesWith(zombie)) {
                    bullet.setActive(false);

                    int healthBefore = zombie.getHealth();
                    zombie.takeDamage((int) bullet.getDamage());
                    state.getMatchStats().addDamageDealt(healthBefore - zombie.getHealth());
                    break;
                }
            }
        }
    }

    private void checkBulletMapCollision (GameState state) {
        boolean[][] map = state.getCollisionMatrix();
        if (map == null) return;

        int tileSize = GameState.TILE_SIZE;

        for (Bullet bullet : state.getPlayerBullets()) {
            if (!bullet.isActive()) continue;

            int tileX = (int) (bullet.getX() + bullet.getWidth() / 2.0) / tileSize;
            int tileY = (int) (bullet.getY() + bullet.getHeight() / 2.0) / tileSize;

            if (tileX >= 0 && tileX < map.length && tileY >= 0 && tileY < map[0].length) {
                if (map[tileX][tileY]) {
                    bullet.setActive(false);
                }
            }
        }

        for (PoisonBullet poison : state.getPoisonBullets()) {
            if (!poison.isActive()) continue;

            int tileX = (int) (poison.getX() + poison.getWidth() / 2.0) / tileSize;
            int tileY = (int) (poison.getY() + poison.getHeight() / 2.0) / tileSize;

            if (tileX >= 0 && tileX < map.length && tileY >= 0 && tileY < map[0].length) {
                if (map[tileX][tileY]) {
                    poison.setActive(false);
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
                poison.applyTo(player);//POLIMORFISMO: cada bala sabe qué hacer
            }
        }
    }

    private void checkZombiePlayerCollision(GameState state) {
    Player player = state.getPlayer();
    if (!player.isAlive()) return;

    for (Enemy zombie : state.getEnemies()) {
        if (!zombie.isAlive()) continue;

        if (zombie.collidesWith(player)) {
            zombie.tryAttack(player);
        } else {
            zombie.cancelWindup(state.getClock().nowMillis());
        }
    }
}
}
