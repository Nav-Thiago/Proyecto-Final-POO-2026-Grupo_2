package model;

import java.time.LocalDate;
import java.util.List;

public class GameModel {

    private final GameState state;
    private final CollisionSystem collisionSystem;
    private final EnemySpawner spawner;
    private final PowerUpSpawner powerUpSpawner;

    public enum GameStatus {PLAYING, PAUSED, BOSS_INTRO, GAME_OVER, GAME_WON}
    private GameStatus status = GameStatus.PLAYING;
    private boolean bossSpawned = false;

    // Intro del jefe: 2 mensajes de INTRO_STEP_MS cada uno
    private static final int INTRO_STEPS = 2;
    private static final long INTRO_STEP_MS = 3500;
    private long bossIntroStart;

    private final MatchStats stats;
    private long lastUpdateTime = System.currentTimeMillis();

    private final MatchDAO matchDAO;

    private long matchTimeMs;
    private long bestTimeMs;
    private boolean newRecord;

    public GameModel(GameState state, MatchDAO matchDAO) {
        this.state = state;
        this.matchDAO = matchDAO;
        this.collisionSystem = new CollisionSystem();
        this.stats = state.getMatchStats();

        // Inicializamos los spawners
        this.spawner = new EnemySpawner(2000, 15);
        this.powerUpSpawner = new PowerUpSpawner(state, 4000, 20, 16); // cada 4 s, máx. 20 en el mapa, 16 al empezar

        //METODO PARA SPAWNEAR EL BOSS EN EL ESTACIONAMIENTO
        /*double estacionamientoX = 6027.192493285801;
        double estacionamientoY = 5056.252234340622;
        state.addEnemy(new FinalBoss(estacionamientoX, estacionamientoY, state.getPoisonBullets()));
        this.bossSpawned = true;*/
    }

    public void update() {
        long now = System.currentTimeMillis();
        long delta = now - lastUpdateTime;
        lastUpdateTime = now;

        if (status == GameStatus.BOSS_INTRO) {
            updateBossIntro();
            return;
        }
        if (status != GameStatus.PLAYING) return;

        stats.addPlayedTime(delta);

        // Mover jugador
        Player player = state.getPlayer();
        if (!player.isAlive()) {
            saveMatch(false);
            status = GameStatus.GAME_OVER;
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
        powerUpSpawner.update();

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

        // Los zombies muertos en este tick cuentan como eliminados
        for (Enemy zombie : state.getEnemies()) {
            if (!zombie.isAlive()) {
                stats.addKill();
            }
        }

        // Limpia los inactivos
        state.purgeInactive();

        // Oleada terminada -> intro del jefe; jefe derrotado -> fin de nivel
        if (state.getEnemies().isEmpty() && spawner.isWaveComplete()) {
            if (!bossSpawned) {
                startBossIntro();
            } else {
                saveMatch(true);
                status = GameStatus.GAME_WON;
            }
        }
    }

    private void startBossIntro() {
        status = GameStatus.BOSS_INTRO;
        bossIntroStart = System.currentTimeMillis();
    }

    private void updateBossIntro() {
        long elapsed = System.currentTimeMillis() - bossIntroStart;
        if (elapsed >= INTRO_STEP_MS * INTRO_STEPS) {
            spawnBoss();
            status = GameStatus.PLAYING;
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
        if (status == GameStatus.PLAYING) {
            status = GameStatus.PAUSED;
        } else if (status == GameStatus.PAUSED) {
            status = GameStatus.PLAYING;
        }
    }

    public void playerInteract() {
        if (status != GameStatus.PLAYING) return;

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

    public void saveMatch(boolean victory) {
        MatchRecord record = new MatchRecord(LocalDate.now(), stats.getPlayedTimes(), stats.getZombiesKilled(), stats.getDamageDealt(), state.getPlayer().getDamageTaken(), victory);

        List<MatchRecord> previousBest = matchDAO.bestSurvivalTimes(1);
        long previousBestMs = previousBest.isEmpty() ? -1 : previousBest.get(0).getSurvivalTimeMs();

        this.matchTimeMs = record.getSurvivalTimeMs();
        this.newRecord = matchTimeMs > previousBestMs;
        this.bestTimeMs = Math.max(matchTimeMs, previousBestMs);

        matchDAO.save(record);
    }

    public long getMatchTimeMs() {
        return matchTimeMs;
    }
    public long getBestTimeMs() {
        return bestTimeMs;
    }
    public boolean isNewRecord() {
        return newRecord;
    }

    public GameState getState() {
        return state;
    }
    public GameStatus getStatus() {
        return status;
    }
}
