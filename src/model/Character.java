package model;

public abstract class Character extends Entity {

    protected double speed;
    private int health;
    private int maxHealth;

    public Character(double x, double y, int width, int height, double speed, int health, int maxHealth) {
        super(x, y, width, height);
        this.speed = speed;
        this.maxHealth = maxHealth;
        this.health = health;
    }

    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    protected void move(double dx, double dy, GameState state) {
        if (dx != 0 || dy != 0) {
            double length = Math.sqrt(dx * dx + dy * dy);
            double moveX = (dx / length) * speed;
            double moveY = (dy / length) * speed;

            if (!isCollidingWithMap(x + moveX, y, state)) x += moveX;
            if (!isCollidingWithMap(x, y + moveY, state)) y += moveY;
        }
    }

    protected boolean isCollidingWithMap(double nextX, double nextY, GameState state) {
        boolean[][] map = state.getCollisionMatrix();
        if (map == null) return false;

        int tile_size = GameState.TILE_SIZE;

        // Hitbox lógica (más chica que sprite visual)
        int leftTile = (int) (nextX + 15) / tile_size;
        int rightTile = (int) (nextX + width - 15) / tile_size;
        int topTile = (int) (nextY + 30) / tile_size;
        int bottomTile = (int) (nextY + height - 5) / tile_size;

        if (leftTile < 0 || rightTile >= map.length || topTile < 0 || bottomTile >= map[0].length) {
            return true;
        }

        return map[leftTile][topTile] || map[rightTile][topTile] ||
           map[leftTile][bottomTile] || map[rightTile][bottomTile];
    }

    protected boolean canMove(double dx, double dy, GameState state) {
        if (dx == 0 && dy == 0) return false;

        double length = Math.hypot(dx, dy);
        double moveX = (dx / length) * speed;
        double moveY = (dy / length) * speed;

        return !isCollidingWithMap(x + moveX, y + moveY, state);
    }

    public void takeDamage(int amount) {
        if (amount < this.health) {
            health -= amount;
        } else {
            this.health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }
}
