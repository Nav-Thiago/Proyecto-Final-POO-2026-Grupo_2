package model;

import java.util.List;
import java.util.Random;

public class EnemySpawner {
    private long lastSpawnTime;
    private int spawnInterval;
    private List<Enemy> enemyList;
    private List<Bullet> poisonList;
    private int zombieSpawned;
    private final int waveLimit;

    public EnemySpawner(List<Enemy> enemyList, List<Bullet> poisonList, int spawnInterval, int waveLimit){
        this.enemyList = enemyList;
        this.poisonList = poisonList;
        this.spawnInterval = spawnInterval;
        this.waveLimit = waveLimit;
        this.lastSpawnTime = System.currentTimeMillis();
        this.zombieSpawned = 0;
    }
    
    public void update(){
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastSpawnTime >= spawnInterval && zombieSpawned < waveLimit) {
            Random random = new Random();
            int tipoZombie = random.nextInt(4);

            Enemy newZombie;

            if (tipoZombie == 0) {
                newZombie = new TankZombie(1000, 300);
            } else if (tipoZombie == 1) {
                newZombie = new FastZombie(1000, 300);
            } else if (tipoZombie == 2) {
                newZombie = new PoisonZombie(1000, 300, poisonList);
            } else {
                newZombie = new NormalZombie(1000, 300);
        }   

            enemyList.add(newZombie);
            lastSpawnTime = currentTime;
            zombieSpawned++;
        }
    }

    public boolean isWaveComplete(){
        return zombieSpawned >= waveLimit;
    }
}
