package model;

import java.util.List;
import java.util.Random;

/**
 * Genera power ups repartidos por todo el mapa, solo en lugares a los que el jugador puede llegar caminando
 * (respeta la matriz de colisiones: nada sobre obstáculos ni dentro de zonas cerradas).
 * Al crearse deja una cantidad inicial y después repone de a uno cada cierto tiempo.
 * Mismo estilo que EnemySpawner.
 */
public class PowerUpSpawner {

    private static final int TYPES = 5;                         // Heal, Shield, DamageBoost, Ammo, MaxAmmo
    private static final int MAX_ATTEMPTS = 200;                // intentos para encontrar un lugar válido
    private static final double MIN_DIST_TO_PLAYER = 250;       // no aparece encima del jugador
    private static final double MIN_DIST_BETWEEN = 150;         // separación mínima entre power ups

    private final GameState state;
    private final ReachableArea reachableArea; // zonas a las que el jugador puede llegar
    private final List<PowerUp> powerUps;
    private final long spawnInterval; // ms entre cada reposición
    private final int maxOnMap;       // tope de power ups simultáneos
    private long lastSpawnTime;
    private final Random random = new Random();

    public PowerUpSpawner(GameState state, long spawnInterval, int maxOnMap, int initialCount) {
        this.state = state;
        // La matriz de colisiones y la posición inicial del jugador ya tienen que estar cargadas acá
        this.reachableArea = new ReachableArea(state);
        if (!reachableArea.isUsable()) {
            System.err.println("PowerUpSpawner: no se pudo calcular la zona alcanzable; se usa solo la matriz de colisiones.");
        }
        this.powerUps = state.getPowerUps();
        this.spawnInterval = spawnInterval;
        this.maxOnMap = maxOnMap;
        this.lastSpawnTime = System.currentTimeMillis();
        populate(Math.min(initialCount, maxOnMap));
    }

    /** Reparte la cantidad inicial recorriendo los tipos en orden, para que haya variedad. */
    private void populate(int count) {
        int firstType = random.nextInt(TYPES);
        for (int i = 0; i < count; i++) {
            trySpawn((firstType + i) % TYPES);
        }
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastSpawnTime < spawnInterval || powerUps.size() >= maxOnMap) return;
        lastSpawnTime = now;
        trySpawn(random.nextInt(TYPES));
    }

    private boolean trySpawn(int type) {
        int mapWidth = state.getMapWidth();
        int mapHeight = state.getMapHeight();
        if (mapWidth <= PowerUp.SIZE || mapHeight <= PowerUp.SIZE) return false;

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            double x = random.nextInt(mapWidth - PowerUp.SIZE);
            double y = random.nextInt(mapHeight - PowerUp.SIZE);
            if (isValidSpot(x, y)) {
                powerUps.add(create(type, x, y));
                return true;
            }
        }
        return false; // no encontró lugar: lo intenta en la próxima reposición
    }

    private boolean isValidSpot(double x, double y) {
        if (isOnBlockedTile(x, y)) return false;

        double centerX = x + PowerUp.SIZE / 2.0;
        double centerY = y + PowerUp.SIZE / 2.0;

        Player player = state.getPlayer();
        if (reachableArea.isUsable() && !reachableArea.canPickUpAt(centerX, centerY, player)) return false;

        double playerX = player.getX() + player.getWidth() / 2.0;
        double playerY = player.getY() + player.getHeight() / 2.0;
        if (Math.hypot(centerX - playerX, centerY - playerY) < MIN_DIST_TO_PLAYER) return false;

        for (PowerUp other : powerUps) {
            double otherX = other.getX() + PowerUp.SIZE / 2.0;
            double otherY = other.getY() + PowerUp.SIZE / 2.0;
            if (Math.hypot(centerX - otherX, centerY - otherY) < MIN_DIST_BETWEEN) return false;
        }
        return true;
    }

    /** true si el power up toca algún tile bloqueado del mapa o se sale de él. */
    private boolean isOnBlockedTile(double x, double y) {
        boolean[][] map = state.getCollisionMatrix();
        if (map == null) return false;

        int tile = GameState.TILE_SIZE;
        int fromX = (int) x / tile;
        int toX = (int) (x + PowerUp.SIZE - 1) / tile;
        int fromY = (int) y / tile;
        int toY = (int) (y + PowerUp.SIZE - 1) / tile;

        for (int tx = fromX; tx <= toX; tx++) {
            for (int ty = fromY; ty <= toY; ty++) {
                if (tx < 0 || tx >= map.length || ty < 0 || ty >= map[0].length) return true;
                if (map[tx][ty]) return true;
            }
        }
        return false;
    }

    private PowerUp create(int type, double x, double y) {
        return switch (type) {
            case 0 -> new Heal(x, y, 25);
            case 1 -> new Shield(x, y, 50);
            case 2 -> new DamageBoost(x, y, 10000);
            case 3 -> new Ammo(x, y);
            default -> new MaxAmmo(x, y);
        };
    }
}
