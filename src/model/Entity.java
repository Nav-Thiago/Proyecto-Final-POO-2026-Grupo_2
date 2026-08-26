package model;

import java.awt.*;

public abstract class Entity {

    protected double x;
    protected double y;
    protected int width;
    protected int height;

    public Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public boolean colisionaCon(Entity otra) {
        if (otra == null) return false;

        return this.x < otra.getX() + otra.getWidth() &&
           this.x + this.width > otra.getX() &&
           this.y < otra.getY() + otra.getHeight() &&
           this.y + this.height > otra.getY();
    }
}
