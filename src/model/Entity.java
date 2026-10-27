 package model;

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

    public boolean collidesWith(Entity e) {
        if (e == null) return false;

        return this.x < e.getX() + e.getWidth() &&
           this.x + this.width > e.getX() &&
           this.y < e.getY() + e.getHeight() &&
           this.y + this.height > e.getY();
    }
}
