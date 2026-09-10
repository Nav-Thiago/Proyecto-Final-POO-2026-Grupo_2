package model;

import java.util.List;

public class EnemySpawner {
    private long lastSpawnTime;
    private int spawnInterval;
    private List<Enemy> enemyList;
    private int zombieSpawned;
    private final int waveLimit;

    public EnemySpawner(List<Enemy> enemyList, int spawnInterval, int waveLimit){
        this.enemyList = enemyList;
        this.spawnInterval = spawnInterval;
        this.waveLimit = waveLimit;
        this.lastSpawnTime = System.currentTimeMillis();
        this.zombieSpawned = 0;
    }
    public void update(){
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastSpawnTime >= spawnInterval && zombieSpawned < waveLimit){
            Enemy newZombie = new NormalZombie(1000,300);
            enemyList.add(newZombie);
            lastSpawnTime = currentTime;
            zombieSpawned++;
        }
    }

    public boolean isWaveComplete(){
        return zombieSpawned >=waveLimit;
    }
}
