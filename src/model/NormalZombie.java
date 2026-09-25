package model;

public class NormalZombie extends Enemy {

    public NormalZombie(double x, double y) {
        super(x, y, 48, 48, 3.0, 100, 100);
    }

    @Override
    public int attack() {
        return 10;
    }
}
