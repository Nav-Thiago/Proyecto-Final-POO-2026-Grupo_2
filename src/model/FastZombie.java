package model;

public class FastZombie extends Enemy {

    public FastZombie(int x, int y) {
        super(x, y, 40, 64, 3.0, 50, 50);
    }

    @Override
    public int attack() {
        return 25;
    }

    @Override
    protected int getAttackCooldown() {
        return 400;
    }

    @Override
    protected int getAttackWindup() {
        return 250;
    }

    @Override
    protected int getAttackAnimMs() {
        return 500;
    }
}
