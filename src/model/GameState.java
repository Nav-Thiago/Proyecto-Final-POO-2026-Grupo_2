package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class GameState {

    private final Player player;
    private final List<Enemy> enemies;
    private final List<Bullet> playerBullets;
    private final List<PoisonBullet> poisonBullets;
    private final List<WeaponItem> groundWeapons;
    private final List<PowerUp> powerUps;

    private int mapWidth;
    private int mapHeight;

    public static final int TILE_SIZE = 32;
    private boolean[][] collisionMatrix;

    private final MatchStats matchStats = new MatchStats();

    private final Queue<SoundEvent> soundEvents = new ConcurrentLinkedQueue<>(); 

    public GameState(Player player, int mapWidth, int mapHeight) {
        this.player = player;
        this.enemies = new CopyOnWriteArrayList<>();
        this.playerBullets = new CopyOnWriteArrayList<>();
        this.poisonBullets = new CopyOnWriteArrayList<>();
        this.groundWeapons = new CopyOnWriteArrayList<>();
        this.powerUps = new CopyOnWriteArrayList<>();
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void setMapDimensions(int width, int height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }

    // getters
    public Player getPlayer() {
        return player;
    }
    public List<Enemy> getEnemies() {
        return enemies;
    }
    public List<Bullet> getPlayerBullets() {
        return playerBullets;
    }
    public List<PoisonBullet> getPoisonBullets() {
        return poisonBullets;
    }
    public List<WeaponItem> getGroundWeapons() {
        return groundWeapons;
    }
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
    public int getMapWidth() {
        return mapWidth;
    }
    public int getMapHeight(){
        return mapHeight;
    }
    public MatchStats getMatchStats() {
        return matchStats;
    }
    public GameClock getClock() {
        return player.getClock();
    }

    public void setCollisionMatrix(boolean[][] matrix) {
        this.collisionMatrix = matrix;
    }
    public boolean[][] getCollisionMatrix() {
        return collisionMatrix;
    }

    public void addPlayerBullet (Bullet b) {
        this.playerBullets.add(b);
    }
    public void addPoisonBullet (PoisonBullet b) {
        this.poisonBullets.add(b);
    }
    public void addEnemy (Enemy e) {
        this.enemies.add(e);
    }

    public void purgeInactive() {
        enemies.removeIf(zombie -> !zombie.isAlive());
        playerBullets.removeIf(bullet -> !bullet.isActive());
        poisonBullets.removeIf(bullet -> !bullet.isActive());
        powerUps.removeIf(powerUp -> !powerUp.isActive());
    }

    // añadir sonidos a la cola de eventos
    public void emitSound(SoundEvent event) {
        if (event != null) {
            soundEvents.add(event);
        }
    }

    // quitar sonidos de la cola de eventos
    public SoundEvent pollSound() {
        return soundEvents.poll();
    }
}
