package model;

public class Player extends Character {

    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;

    // Constantes para el límite del mapa
    private final int mapWidth = 800;
    private final int mapHeight = 600;

    public Player(double x, double y) {
        super(x, y, 64, 64, 7.0, 100, 100);
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
}
