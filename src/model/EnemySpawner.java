package model;

import java.util.Random;

public class EnemySpawner {
    private long lastSpawnTime;
    private int spawnInterval;
    private int zombieSpawned;
    private final int waveLimit;

    public EnemySpawner(int spawnInterval, int waveLimit){
        this.spawnInterval = spawnInterval;
        this.waveLimit = waveLimit;
        this.lastSpawnTime = System.currentTimeMillis();
        this.zombieSpawned = 0;
    }
    
    public void update(GameState state){
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastSpawnTime >= spawnInterval && zombieSpawned < waveLimit) {
            Random random = new Random();
            int tipoZombie = random.nextInt(4);
            int mapWidth = state.getMapWidth();
            int mapHeight = state.getMapHeight();

        
            if (mapWidth <= 100 || mapHeight <= 100) return;

            int spawnX = random.nextInt(mapWidth - 100);
            int spawnY = random.nextInt(mapHeight - 100);

            Enemy newZombie;

            if (tipoZombie == 0) {
                newZombie = new TankZombie(spawnX, spawnY);
            } else if (tipoZombie == 1) {
                newZombie = new FastZombie(spawnX, spawnY);
            } else if (tipoZombie == 2) {
                newZombie = new PoisonZombie(spawnX, spawnY, state.getPoisonBullets());
            } else {
                newZombie = new NormalZombie(spawnX, spawnY);
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
