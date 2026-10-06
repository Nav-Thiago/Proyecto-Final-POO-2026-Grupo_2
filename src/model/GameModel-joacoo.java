package model;

import java.util.List;

public class GameModel {

    private final GameState state;
    private final CollisionSystem collisionSystem;
    private final EnemySpawner spawner;
    private final PowerUpSpawner powerUpSpawner;

    public enum gameStatus {PLAYING, PAUSED, LEVEL_COMPLETED, GAME_OVER, GAME_WON}
    private gameStatus status = gameStatus.PLAYING;
    private int actualLevel = 1;
    private final int totalLevels = 2;

    public GameModel(GameState state) {
        this.state = state;
        this.collisionSystem = new CollisionSystem();

        // Inicializamos los spawners
        this.spawner = new EnemySpawner(2000, 2);
        this.powerUpSpawner = new PowerUpSpawner(state.getPowerUps(), 8000, 3);
        System.out.println("GameModel NUEVO cargado");
        state.addEnemy(new FinalBoss(1000, 300, state.getPoisonBullets())); //PRUEBA PARA JEFE ZOMBIE FINAL
    }

    public void update(int screenWidth, int screenHeight) {
        if (status != gameStatus.PLAYING) return;

        // Mover jugador
        Player player = state.getPlayer();
        if (!player.isAlive()) {
            status = gameStatus.GAME_OVER;
            return;
        }

        player.update(state);

        int mapWidth = state.getMapWidth();
        int mapHeight = state.getMapHeight();

        // Mover balas
        for (Bullet b : state.getPlayerBullets()) b.update(mapWidth, mapHeight);
        for (Bullet p: state.getPoisonBullets()) p.update(mapWidth, mapHeight);

        // Spawners
        spawner.update(state);
        powerUpSpawner.update(screenWidth, screenHeight);

        // Mover zombies
        for (Enemy zombie : state.getEnemies()) {
            if (zombie.isAlive()) {
                zombie.update(player, state);
            }
        }

        // Recolección de power-ups
        for (PowerUp p : state.getPowerUps()) {
            if (p.isActive() && player.collidesWith(p)) {
                p.collect(player);
            }
        }

        // Sistema de colisiones (daño)
        collisionSystem.update(state);

        // Limpia los inactivos
        state.purgeInactive();

        // Chequeo fin de nivel
        if (state.getEnemies().isEmpty() && spawner.isWaveComplete()) {
            if (actualLevel < totalLevels) {
                status = gameStatus.LEVEL_COMPLETED; 
            } else {
                status = gameStatus.GAME_WON;
            }
        }
    }

    public void togglePause() {
        if (status == gameStatus.PLAYING) {
            status = gameStatus.PAUSED;
        } else if (status == gameStatus.PAUSED) {
            status = gameStatus.PLAYING;
        }
    }

    public void playerInteract() {
        if (status != gameStatus.PLAYING) return;

        List<WeaponItem> groundWeapons = state.getGroundWeapons();
        Player player = state.getPlayer();

        for (int i = 0; i < groundWeapons.size(); i++) {
            WeaponItem item = groundWeapons.get(i);

            if (player.collidesWith(item)) {
                FireArm oldWeapon = player.getPrimaryWeapon();
                player.equipWeapon(item.getWeapon());
                groundWeapons.remove(i);

                if (oldWeapon != null) {
                    WeaponItem droppedWeapon = new WeaponItem(player.getX(), player.getY(), oldWeapon);
                    groundWeapons.add(droppedWeapon);
                }
                break;
            }
        }
    }

    public void nextLevel() {
        if (status != gameStatus.LEVEL_COMPLETED) return;
        actualLevel++;
        state.purgeInactive();
        status = gameStatus.PLAYING;
    }

    public GameState getState() {
        return state;
    }
    public gameStatus getStatus() {return status;}

    
}