package model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameModel {

    private final Player player;
    private final List<Enemy> enemies;
    private final List<Bullet> playerBullets;
    private final List<Bullet> poisonBullets;
    private final EnemySpawner spawner;

    // [POWERUP] Agregamos las listas y el spawner del compañero
    private final List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
    private final PowerUpSpawner powerUpSpawner;

    public enum gameStatus {
        PLAYING, LEVEL_COMPLETED, GAME_OVER, GAME_WON
    }
    private gameStatus status = gameStatus.PLAYING;
    private int actualLevel = 1;
    private final int totalLevels = 3;

    public GameModel(Player player, List<Enemy> enemies, List<Bullet> playerBullets, List<Bullet> poisonBullets) {
        this.player = player;
        this.enemies = enemies;
        this.playerBullets = playerBullets;
        this.poisonBullets = poisonBullets;
        
        // Inicializamos los spawners
        this.spawner = new EnemySpawner(this.enemies, this.poisonBullets, 2000, 10);
        this.powerUpSpawner = new PowerUpSpawner(this.powerUps, 8000, 3); // [POWERUP] 
    }

    public void update(int screenWidth, int screenHeight) {
        if (status != gameStatus.PLAYING) return;

        if (!player.isAlive()) {
            status = gameStatus.GAME_OVER;
            return;
        }

        player.update(screenWidth, screenHeight);
        spawner.update();

        // [POWERUP] Físicas y recolección de los potenciadores
        powerUpSpawner.update(screenWidth, screenHeight);
        for (PowerUp p : powerUps) {
            if (p.isActive() && player.collidesWith(p)) {
                p.collect(player);
            }
        }
        powerUps.removeIf(p -> !p.isActive());

        for (Enemy zombie : enemies) {
            if (zombie.isAlive()) {
                zombie.update(player);

                for (Bullet bullet : playerBullets) {
                    bullet.update(screenWidth, screenHeight);
                    if (bullet.isActive() && bullet.collidesWith(zombie)) {
                        bullet.setActive(false);
                        zombie.takeDamage((int) bullet.getDamage());
                        System.out.println("Zombie impactado, vida del zombie: " + zombie.getHealth());
                    }
                }
            }
        }

        for (Bullet poison : poisonBullets) {
            poison.update(screenWidth, screenHeight);

            if (poison.isActive() && player.isAlive() && poison.getBounds().intersects(player.getBounds())) {
                poison.setActive(false);
                player.takeDamage((int) poison.getDamage());
            }
        }

        playerBullets.removeIf(b -> !b.isActive());
        poisonBullets.removeIf(b -> !b.isActive());
        enemies.removeIf(z -> !z.isAlive());

        if (enemies.isEmpty() && spawner.isWaveComplete()) {
            if (actualLevel < totalLevels) {
                status = gameStatus.LEVEL_COMPLETED;
            } else {
                status = gameStatus.GAME_WON;
            }
        }
    }

    public void nextLevel() {
        if (status != gameStatus.LEVEL_COMPLETED) return;
        actualLevel++;
        enemies.clear();
        enemies.add(new NormalZombie(600, 300));
        playerBullets.clear();
        poisonBullets.clear();
        powerUps.clear(); // [POWERUP] Limpieza en el cambio de nivel
        status = gameStatus.PLAYING;
    }

    // Getters
    public Player getPlayer() { return player; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Bullet> getPlayerBullets() { return playerBullets; }
    public List<Bullet> getPoisonBullets() { return poisonBullets; }
    public List<PowerUp> getPowerUps() { return powerUps; } // [POWERUP]
    public gameStatus getStatus() { return status; }
}