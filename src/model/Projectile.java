package model;

import java.awt.Rectangle; 

public class Projectile extends Entity {
    private double dirX;
    private double dirY;
    private double speed;
    private double damage;
    private boolean active; // booleano para saber si la bala debe 'desaparecer'

    public Projectile(double x, double y, double dirX, double dirY, double speed, double damage) {
        super(x, y, 6, 6);
        this.dirX = dirX;
        this.dirY = dirY;
        this.speed = speed;
        this.damage = damage;
        this.active = true;
    }

    
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public double getDamage(){
        return damage;
    }

    public void update(int mapWidth, int mapHeight) {
        this.x += dirX * speed; 
        this.y += dirY * speed;

        // Desactivar la bala si sale de los limites de la ventana
        if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
            active = false;
        }
    }

    public Rectangle getBounds(){
        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }

}
