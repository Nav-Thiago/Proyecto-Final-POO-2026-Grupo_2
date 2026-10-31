package model;

public class Bullet extends Entity {
    private final double dirX;
    private final double dirY;
    private final double speed;
    private final double damage;
    private final BulletKind kind;
    private boolean active; // booleano para saber si la bala debe 'desaparecer'
    private int lifeSpanTicks;
    private int age; // ticks que lleva viva (la vista lo usa para que la estela "salga" del arma)

    public Bullet(double x, double y, int width, int height, double dirX, double dirY, double damage, int lifeSpanTicks) {
        this(x, y, width, height, dirX, dirY, damage, lifeSpanTicks, BulletKind.STANDARD);
    }

    public Bullet(double x, double y, int width, int height, double dirX, double dirY, double damage, int lifeSpanTicks, BulletKind kind) {
        super(x, y, width, height);
        this.dirX = dirX;
        this.dirY = dirY;
        this.speed = 12.0;
        this.damage = damage;
        this.active = true;
        this.lifeSpanTicks = lifeSpanTicks;
        this.kind = kind;
        this.age = 0;
    }

    public void update(int mapWidth, int mapHeight) {
        this.x += dirX * speed; 
        this.y += dirY * speed;

        this.age++;
        this.lifeSpanTicks--;

        if (this.lifeSpanTicks <= 0) {
            this.active = false;
        }

        // Desactivar la bala si sale de los límites de la ventana
        if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
            active = false;
        }
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
    public double getDirX() {
        return dirX;
    }
    public double getDirY() {
        return dirY;
    }
    public BulletKind getKind() {
        return kind;
    }
    public int getAge() {
        return age;
    }
    public double getSpeed() {
        return speed;
    }
}
