package model;

public class Player extends Character {

    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;

    // Constantes para el límite del mapa
    private final int mapWidth = 1280;
    private final int mapHeight = 720;

    private double angle; // Ángulo en radianes hacia el cursor 

    public Player(double x, double y) {
        super(x, y, 64, 64, 5.5, 100, 100);
    }

    public void setMoveUp(boolean moveUp) {
        this.moveUp = moveUp;
    }

    public void setMoveDown(boolean moveDown) {
        this.moveDown = moveDown;
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public void update(int mapWidth, int mapHeight) {
        double dx = 0;
        double dy = 0;
        if (moveUp) dy -= 1;
        if (moveDown) dy += 1;
        if (moveLeft) dx -= 1;
        if (moveRight) dx += 1;

        move(dx, dy);

        if (x < 0) x = 0;
        if (x + width > mapWidth) x = mapWidth - width;
        if (y < 0) y = 0;
        if (y + height > mapHeight) y = mapHeight - height;
    }

    public void aimAt(double mouseX, double mouseY) {
        //Centro real del personaje
        double centroX = this.getX() + (this.getWidth() / 2.0);
        double centroY = this.getY() + (this.getHeight() / 2.0);

        double deltaX = mouseX - centroX;
        double deltaY = mouseY - centroY;

        this.angle = Math.atan2(deltaY, deltaX);
    }
    //Getter para obtener el ángulo
    public double getAngle() {
        return angle;
    }

    public Projectile shoot() {
        // Punto desde donde sale la bala
        double origenX = this.x + (this.width / 2.0);
        double origenY = this.y + (this.height / 2.0);

        double dirX = Math.cos(this.angle);
        double dirY = Math.sin(this.angle);

        return new Projectile(origenX, origenY, dirX, dirY, 12.0, 25.0);
    }
}
