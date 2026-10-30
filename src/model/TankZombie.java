package model;

public class TankZombie extends Enemy {

    public TankZombie(int x, int y) {
        super(x, y, 86, 86, 1.0, 250, 250);
    }

    @Override
    public int attack() {
        return 15;
    }
}
