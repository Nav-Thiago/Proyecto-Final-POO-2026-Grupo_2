package model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameModel {

    private final Player player;
    private final List<Enemy> enemies;
    private final List<Bullet> playerBullets;
    private final List<Bullet> poisonBullets;
    private final List<WeaponItem> groundWeapons;
    private final EnemySpawner spawner;

    private final List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
    private final PowerUpSpawner powerUpSpawner;

    public enum gameStatus {
        PLAYING, PAUSED, LEVEL_COMPLETED, GAME_OVER, GAME_WON
    }
    private gameStatus status = gameStatus.PLAYING;

    public void togglePause() {
        if (status == gameStatus.PLAYING) {
            status = gameStatus.PAUSED;
        } else if (status == gameStatus.PAUSED) {
            status = gameStatus.PLAYING;
        }
    }
    
    private int actualLevel = 1;
    private final int totalLevels = 3;

    public GameModel(Player player, List<Enemy> enemies, List<Bullet> playerBullets, List<Bullet> poisonBullets, List<WeaponItem> groundWeapons) {
        this.player = player;
        this.enemies = enemies;
        this.playerBullets = playerBullets;
        this.poisonBullets = poisonBullets;
        this.groundWeapons = groundWeapons;
        
        // Inicializamos los spawners
        this.spawner = new EnemySpawner(this.enemies, this.poisonBullets, 2000, 3);
        this.powerUpSpawner = new PowerUpSpawner(this.powerUps, 8000, 3);
    }

    public void playerInteract() {
        if (status != gameStatus.PLAYING) return;

        for (int i = 0; i < this.groundWeapons.size(); i++) {
            WeaponItem item = this.groundWeapons.get(i);

            if (this.player.collidesWith(item)) {
                FireArm oldWeapon = this.player.getPrimaryWeapon();
                this.player.equipWeapon(item.getWeapon());
                this.groundWeapons.remove(i);

                if (oldWeapon != null) {
                    WeaponItem droppedWeapon = new WeaponItem(this.player.getX(), this.player.getY(), oldWeapon);
                    this.groundWeapons.add(droppedWeapon);
                }
                break;
            }
        }
    }

    public void update(int screenWidth, int screenHeight) {
        if (status != gameStatus.PLAYING) return;

        if (!player.isAlive()) {
            status = gameStatus.GAME_OVER;
            return;
        }

        player.update(screenWidth, screenHeight);
        spawner.update();

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

                if (zombie.getBounds().intersects(player.getBounds())) {
                    
                    double dx = zombie.getX() - player.getX();
                    double dy = zombie.getY() - player.getY();
                    
                    if (dx == 0 && dy == 0) {
                        dx = Math.random() - 0.5;
                        dy = Math.random() - 0.5;
                    }
                    
                    double distancia = Math.sqrt(dx * dx + dy * dy);
                    double repulsionJugador = 2.5; 
                    
                    zombie.setX(zombie.getX() + (dx / distancia) * repulsionJugador);
                    zombie.setY(zombie.getY() + (dy / distancia) * repulsionJugador);
                }

                for (Enemy otroZombie : enemies) { 
                    if (zombie != otroZombie && otroZombie.isAlive()) { 
                        
                        if (zombie.getBounds().intersects(otroZombie.getBounds())) { 
                            
                            double dx = zombie.getX() - otroZombie.getX(); 
                            double dy = zombie.getY() - otroZombie.getY(); 
                            
                            if (dx == 0 && dy == 0) { 
                                dx = Math.random() - 0.5; 
                                dy = Math.random() - 0.5; 
                            }
                            
                            double distancia = Math.sqrt(dx * dx + dy * dy); 
                            double repulsion = 2.0; 
                            
                            zombie.setX(zombie.getX() + (dx / distancia) * repulsion); 
                            zombie.setY(zombie.getY() + (dy / distancia) * repulsion); 
                        }
                    }
                }

                for (Bullet bullet : playerBullets) { 
                    bullet.update(screenWidth, screenHeight); 
                    if (bullet.isActive() && bullet.collidesWith(zombie)) { 
                        bullet.setActive(false); 
                        zombie.takeDamage((int) bullet.getDamage());
                    }
                }
            }
        }

        for (Bullet poison : poisonBullets) { 
            poison.update(screenWidth, screenHeight); 

            if (poison.isActive() && player.isAlive() && poison.getBounds().intersects(player.getBounds())) { 
                poison.setActive(false); 
                player.addEffect(new PoisonEffect(4000)); 
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
    public Player getPlayer() {return player;}

    public List<Enemy> getEnemies() {return enemies;}

    public List<Bullet> getPlayerBullets() {return playerBullets;}

    public List<Bullet> getPoisonBullets() {return poisonBullets;}

    public List<PowerUp> getPowerUps() {return powerUps;}

    public List<WeaponItem> getGroundWeapons() {
        return groundWeapons;
    }

    public gameStatus getStatus() {return status;}
}