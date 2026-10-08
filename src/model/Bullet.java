package model;

import java.awt.Rectangle; 

public class Bullet extends Entity {
    private final double dirX;
    private final double dirY;
    private final double speed;
    private final double damage;
    private boolean active; // booleano para saber si la bala debe 'desaparecer'
    private int lifeSpanTicks;

    public Bullet(double x, double y, int width, int height, double dirX, double dirY, double damage, int lifeSpanTicks) {
        super(x, y, width, height);
        this.dirX = dirX;
        this.dirY = dirY;
        this.speed = 12.0;
        this.damage = damage;
        this.active = true;
        this.lifeSpanTicks = lifeSpanTicks;
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

        this.lifeSpanTicks--;

        if (this.lifeSpanTicks <= 0) {
            this.active = false;
        }

        // Desactivar la bala si sale de los límites de la ventana
        if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
            active = false;
        }
    }

    public Rectangle getBounds(){
        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }
}
