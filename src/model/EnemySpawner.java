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
            boolean[][] collisionMatrix = state.getCollisionMatrix();
            int tileSize = GameState.TILE_SIZE;

            if (mapWidth <= 100 || mapHeight <= 100 || collisionMatrix == null) return;

            int spawnX = 0;
            int spawnY = 0;
            boolean validPosition = false;
            int maxAttempts = 50; // Evita un bucle infinito si el mapa estuviera lleno
            int attempts = 0;

            // Buscamos una coordenada que no sea colisión
            while (!validPosition && attempts < maxAttempts) {
                spawnX = random.nextInt(mapWidth - 100);
                spawnY = random.nextInt(mapHeight - 100);
                
                int tileX = spawnX / tileSize;
                int tileY = spawnY / tileSize;

                // Verificamos que esté dentro de los límites de la matriz
                if (tileX >= 0 && tileX < collisionMatrix.length && 
                    tileY >= 0 && tileY < collisionMatrix[0].length) {
                    
                    // Si es false, significa que no hay colisión (es caminable)
                    if (!collisionMatrix[tileX][tileY]) {
                        validPosition = true;
                    }
                }
                attempts++;
            }

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

    public void reset() {
    this.zombieSpawned = 0; 
    this.lastSpawnTime = System.currentTimeMillis();
    }
}