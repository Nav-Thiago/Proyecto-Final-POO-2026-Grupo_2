package model;

import java.util.List;

public class GameModel {

    private final GameState state;
    private final CollisionSystem collisionSystem;
    private final EnemySpawner spawner;
    private final PowerUpSpawner powerUpSpawner;

    public enum gameStatus {PLAYING, PAUSED, BOSS_INTRO, LEVEL_COMPLETED, GAME_OVER, GAME_WON}
    private gameStatus status = gameStatus.PLAYING;
    private int actualLevel = 1;
    private final int totalLevels = 2;
    private boolean bossSpawned = false;

    // Intro del jefe: 2 mensajes de INTRO_STEP_MS cada uno
    private static final int INTRO_STEPS = 2;
    private static final long INTRO_STEP_MS = 3500;
    private long bossIntroStart;

    public GameModel(GameState state) {
        this.state = state;
        this.collisionSystem = new CollisionSystem();

        // Inicializamos los spawners
        this.spawner = new EnemySpawner(2000, 10);
        this.powerUpSpawner = new PowerUpSpawner(state.getPowerUps(), 8000, 3);
       // state.addEnemy(new FinalBoss(1000, 300, state.getPoisonBullets())); //PRUEBA PARA JEFE ZOMBIE FINAL
    }

    public void update(int screenWidth, int screenHeight) {
        if (status == gameStatus.BOSS_INTRO) {
            updateBossIntro();
            return;
        }
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
        for (Bullet b : state.getPlayerBullets()) b.update(screenWidth, screenHeight);
        for (Bullet p : state.getPoisonBullets()) p.update(screenWidth, screenHeight);
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

        // Oleada terminada -> intro del jefe; jefe derrotado -> fin de nivel
        if (state.getEnemies().isEmpty() && spawner.isWaveComplete()) {
            if (!bossSpawned) {
                startBossIntro();
            } else if (actualLevel < totalLevels) {
                status = gameStatus.LEVEL_COMPLETED;
            } else {
                status = gameStatus.GAME_WON;
            }
        }
    }

    private void startBossIntro() {
        status = gameStatus.BOSS_INTRO;
        bossIntroStart = System.currentTimeMillis();
    }

    private void updateBossIntro() {
        long elapsed = System.currentTimeMillis() - bossIntroStart;
        if (elapsed >= INTRO_STEP_MS * INTRO_STEPS) {
            spawnBoss();
            status = gameStatus.PLAYING;
        }
    }

    private void spawnBoss() {
        state.addEnemy(new FinalBoss(1000, 300, state.getPoisonBullets()));
        bossSpawned = true;
    }

    /** Qué mensaje toca mostrar: 0 = felicitación, 1 = "Preparate para el Jefe". */
    public int getBossIntroStep() {
        long elapsed = System.currentTimeMillis() - bossIntroStart;
        return (int) Math.min(INTRO_STEPS - 1, elapsed / INTRO_STEP_MS);
    }

    /** Avance (0.0 a 1.0) dentro del mensaje actual, para el efecto de aparecer y desvanecer. */
    public double getBossIntroProgress() {
        long elapsed = System.currentTimeMillis() - bossIntroStart;
        return Math.min(1.0, (elapsed % INTRO_STEP_MS) / (double) INTRO_STEP_MS);
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
        bossSpawned = false;
        spawner.reset();
        state.purgeInactive();
        status = gameStatus.PLAYING;
    }

    public GameState getState() {
        return state;
    }
    public gameStatus getStatus() {
        return status;
    }
}