package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameState {

    private final Player player;
    private final List<Enemy> enemies;
    private final List<Bullet> playerBullets;
    private final List<Bullet> poisonBullets;
    private final List<WeaponItem> groundWeapons;
    private final List<PowerUp> powerUps;

    private int mapWidth;
    private int mapHeight;

    public GameState(Player player, int mapWidth, int mapHeight) {
        this.player = player;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.enemies = new ArrayList<>();
        this.playerBullets = new ArrayList<>();
        this.poisonBullets = new ArrayList<>();
        this.groundWeapons = new ArrayList<>();
        this.powerUps = new ArrayList<>();
    }

    // getters
    public int getMapWidth() {
        return mapWidth;
    }
    public int getMapHeight() {
        return mapHeight;
    }
    public Player getPlayer() {
        return player;
    }
    public List<Enemy> getEnemies() {
        return enemies;
    }
    public List<Bullet> getPlayerBullets() {
        return playerBullets;
    }
    public List<Bullet> getPoisonBullets() {
        return poisonBullets;
    }
    public List<WeaponItem> getGroundWeapons() {
        return groundWeapons;
    }
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }


    public void addPlayerBullet (Bullet b) {
        this.playerBullets.add(b);
    }
    public void addPoisonBullet (Bullet b) {
        this.poisonBullets.add(b);
    }
    public void addEnemy (Enemy e) {
        this.enemies.add(e);
    }


    public void purgeInactive() {
        enemies.clear();
        playerBullets.clear();
        poisonBullets.clear();
        powerUps.clear();
    }
}
