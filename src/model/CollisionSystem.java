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
                    zombie.takeDamage((int) bullet.getDamage());
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
                // 1. Si es el primer instante en que choca, inicia el temporizador del choque
                if (!zombie.isCollidingWithPlayer()) {
                    zombie.setCollidingWithPlayer(true);
                    zombie.setCollisionStartTime(System.currentTimeMillis());
                }

                // 2. Debe tener su intervalo de ataque listo Y haber mantenido el choque por 400ms
                if (zombie.canAttack() && zombie.hasCompletedAttackDelay()) {
                    player.takeDamage(zombie.attack());
                    zombie.resetAttackCooldown();
                }
            } else {
                // 3. Si el jugador se aleja antes de los 400ms, se reinicia el choque
                zombie.setCollidingWithPlayer(false);
            }
        }
    }
}
