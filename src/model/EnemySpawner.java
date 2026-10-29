package model;

import java.util.Random;

public class EnemySpawner {
    private long lastSpawnTime;
    private int spawnInterval;
    private int zombieSpawned;
    private final int waveLimit;
    private final GameClock clock;

    public EnemySpawner(int spawnInterval, int waveLimit, GameClock clock){
        this.spawnInterval = spawnInterval;
        this.waveLimit = waveLimit;
        this.lastSpawnTime = clock.nowMillis();
        this.zombieSpawned = 0;
        this.clock = clock;
    }
    
    public void update(GameState state){
        long currentTime = clock.nowMillis();

        if (currentTime - lastSpawnTime >= spawnInterval && zombieSpawned < waveLimit) {
            Random random = new Random();
            int tipoZombie = random.nextInt(4);

            int mapWidth = state.getMapWidth();
            int mapHeight = state.getMapHeight();
            boolean[][] collisionMatrix = state.getCollisionMatrix();

            if (mapWidth <= 100 || mapHeight <= 100 || collisionMatrix == null) return;

            int spawnX = 0;
            int spawnY = 0;
            boolean validPosition = false;
            int maxAttempts = 50; 
            int attempts = 0;
            int maxZombieWidth = 86;
            int maxZombieHeight = 86;

            while (!validPosition && attempts < maxAttempts) {
                spawnX = random.nextInt(mapWidth - maxZombieWidth);
                spawnY = random.nextInt(mapHeight - maxZombieHeight);
                if (!isOnBlockedTile(spawnX, spawnY, maxZombieWidth, maxZombieHeight, collisionMatrix)) {
                    validPosition = true;
                }
                attempts++;
            }

            Enemy newZombie;

            if (tipoZombie == 0) {
                newZombie = new TankZombie(spawnX, spawnY);
            } else if (tipoZombie == 1) {
                newZombie = new FastZombie(spawnX, spawnY);
            } else if (tipoZombie == 2) {
                newZombie = new PoisonZombie(spawnX, spawnY, state.getPoisonBullets(), state.getClock());
            } else {
                newZombie = new NormalZombie(spawnX, spawnY);
            }

            state.addEnemy(newZombie);
            lastSpawnTime = currentTime;
            zombieSpawned++;
        }
    }

    // Método extraído de la lógica del PowerUpSpawner
    private boolean isOnBlockedTile(double x, double y, int width, int height, boolean[][] map) {
        int tile = GameState.TILE_SIZE;
        int fromX = (int) x / tile;
        int toX = (int) (x + width - 1) / tile;
        int fromY = (int) y / tile;
        int toY = (int) (y + height - 1) / tile;

        for (int tx = fromX; tx <= toX; tx++) {
            for (int ty = fromY; ty <= toY; ty++) {
                if (tx < 0 || tx >= map.length || ty < 0 || ty >= map[0].length) return true;
                if (map[tx][ty]) return true; // Hay colisión
            }
        }
        return false; // Área completamente libre
    }

    public boolean isWaveComplete(){
        return zombieSpawned >= waveLimit;
    }

    public void reset() {
        this.zombieSpawned = 0; 
        this.lastSpawnTime = clock.nowMillis();
    }
}