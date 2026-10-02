package model;

import java.util.List;

public class PoisonZombie extends Enemy {
    
    private List<Bullet> poisonList;
    private long lastShootTime;

    public PoisonZombie(double x, double y, List<Bullet> poisonList){
        super(x, y, 48, 48, 2.0, 80, 80);
        this.poisonList = poisonList;
        this.lastShootTime = System.currentTimeMillis();
    }

    @Override 
    public int attack(){
        return 5;
    }
    
    @Override 
    public void update(Player player, GameState state){
        
        double dx = player.getX() - this.getX();
        double dy = player.getY() - this.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 300) {
            super.update(player, state);
        }else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime >= 1500) {
                shoot(player);
                lastShootTime = currentTime; 
            }
        }
    }

    private void shoot(Player player) {
    
        double dx = player.getX() - this.getX();
        double dy = player.getY() - this.getY();
        double distancia = Math.sqrt((dx * dx) + (dy * dy));

        double dirX = 0.0;
        double dirY = 0.0;

        if (distancia > 0) {
            dirX = dx / distancia;
            dirY = dy / distancia;
        }

        Bullet poison = new Bullet(this.getX(), this.getY(), dirX, dirY, 4.0, 10.0);
        poisonList.add(poison);
    }
}
