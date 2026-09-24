package model;

import java.util.List;

public class GameModel {

    private final Player player;
    private final List<Enemy> enemies;
    private final List<Bullet> playerBullets;
    private final List<Bullet> poisonBullets;
    private final EnemySpawner spawner;

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
        this.spawner = new EnemySpawner(this.enemies, this.poisonBullets, 2000, 10);
    }

    // El update ahora recibe las dimensiones de la pantalla por parámetro
    public void update(int screenWidth, int screenHeight) {
        if (status != gameStatus.PLAYING) return;

        if (!player.isAlive()) {
            status = gameStatus.GAME_OVER;
            return;
        }

        player.update(screenWidth, screenHeight);
        spawner.update();

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
        enemies.add(new NormalZombie(600, 300)); // Creamos el primer zombie del nivel
        playerBullets.clear();
        poisonBullets.clear();
        status = gameStatus.PLAYING;
    }

    // Getters para que la Vista (GamePanel) pueda leer los datos y dibujarlos
    public Player getPlayer() { return player; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Bullet> getPlayerBullets() { return playerBullets; }
    public List<Bullet> getPoisonBullets() { return poisonBullets; }
    public gameStatus getStatus() { return status; }
}