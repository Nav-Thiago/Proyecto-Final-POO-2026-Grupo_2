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
                if (!state.isOnBlockedTile(spawnX, spawnY, maxZombieWidth, maxZombieHeight)) {
                    validPosition = true;
                }
                attempts++;
            }

            if (!validPosition) return;

            Enemy newZombie;

            if (tipoZombie == 0) {
                newZombie = new Enemy(spawnX, spawnY, EnemyType.TANK, 86, 86, 1.5, 250, 250, 15);
            } else if (tipoZombie == 1) {
                newZombie = new Enemy(spawnX, spawnY, EnemyType.FAST, 40, 64, 5.0, 80, 80, 25);
            } else if (tipoZombie == 2) {
                newZombie = new PoisonZombie(spawnX, spawnY, state.getClock());
            } else {
                newZombie = new Enemy(spawnX, spawnY, EnemyType.NORMAL, 64, 64, 3.0, 100, 100, 10);
            }

            state.addEnemy(newZombie);
            lastSpawnTime = currentTime;
            zombieSpawned++;
        }
    }

    public boolean isWaveComplete(){
        return zombieSpawned >= waveLimit;
    }
}