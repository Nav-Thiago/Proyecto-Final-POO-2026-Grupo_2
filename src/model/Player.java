package model;

public class Player extends Entity {

    private double speed = 3.0;

    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;

    // Constantes para el límite del mapa
    private final int mapWidth = 800;
    private final int mapHeight = 600;

    public Player(double x, double y) {
        super(x, y, 32, 32);
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

    public void update() {
        double dx = 0;
        double dy = 0;
        if (moveUp) dy -= 1;
        if (moveDown) dy += 1;
        if (moveLeft) dx -= 1;
        if (moveRight) dx += 1;

        if (dx != 0 || dy != 0) {
            double length = Math.sqrt(dx * dx + dy * dy);
            dx = (dx/length * speed);
            dy = (dy/length * speed);
        }

        double nextX = x + dx;
        double nextY = y + dy; // Se calcula donde estaría el jugador si se moviera

        if (nextX < 0) nextX = 0; // Límite izquierdo
        if (nextX + width > mapWidth) nextX = mapWidth - width; // Límite derecho (restamos el ancho del jugador para que no salga la mitad del cuerpo

        if (nextY < 0) nextY = 0; // Límite superior
        if (nextY + height > mapHeight) nextY = mapHeight - height; // Límite inferior (restamos el alto del jugador)

        x = nextX;
        y = nextY;
    }
}
