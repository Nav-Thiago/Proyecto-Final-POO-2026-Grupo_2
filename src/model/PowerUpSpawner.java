package model;

import java.util.List;
import java.util.Random;

/** Genera power ups en posiciones aleatorias del mapa. Mismo estilo que EnemySpawner. */
public class PowerUpSpawner {

    private final List<PowerUp> powerUps;
    private final long spawnInterval; // ms entre cada spawn
    private final int maxOnMap;       // tope de power ups simultáneos
    private long lastSpawnTime;
    private final Random random = new Random();

    public PowerUpSpawner(List<PowerUp> powerUps, long spawnInterval, int maxOnMap) {
        this.powerUps = powerUps;
        this.spawnInterval = spawnInterval;
        this.maxOnMap = maxOnMap;
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public void update(int mapWidth, int mapHeight) {
        long now = System.currentTimeMillis();
        if (now - lastSpawnTime < spawnInterval || powerUps.size() >= maxOnMap) return;
        lastSpawnTime = now;

        // El panel puede medir 0 antes de mostrarse
        if (mapWidth <= PowerUp.SIZE || mapHeight <= PowerUp.SIZE) return;

        double x = random.nextInt(mapWidth - PowerUp.SIZE);
        double y = random.nextInt(mapHeight - PowerUp.SIZE);
        powerUps.add(createRandom(x, y));
    }

    private PowerUp createRandom(double x, double y) {
        return switch (random.nextInt(5)) {
            case 0 -> new Heal(x, y, 25);
            case 1 -> new Shield(x, y, 50);
            case 2 -> new DamageBoost(x, y, 10000);
            case 3 -> new Ammo(x, y, 30);
            default -> new MaxAmmo(x, y);
        };
    }
}
