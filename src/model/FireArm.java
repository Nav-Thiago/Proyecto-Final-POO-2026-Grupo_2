package model;

import java.util.List;

public abstract class FireArm extends Weapon implements AmmoHolder {

    protected int actualMunition;
    private int maxMunition;

    public FireArm(int damage, long cooldown, int actualMunition, int maxMunition){
        super(damage);
        this.cooldown = cooldown;
        this.actualMunition = actualMunition;
        this.maxMunition = maxMunition;
    }

    @Override
    public List<Bullet> shoot(double x, double y, double angle){
        long currentTime = System.currentTimeMillis();

        if (this.actualMunition > 0 && currentTime - this.lastShoot >= this.cooldown) {

            this.actualMunition -= 1;
            this.lastShoot = currentTime;

            double dirX = Math.cos(angle);
            double dirY = Math.sin(angle);

            return List.of(new Bullet(x, y, dirX, dirY, 12.0, this.getDamage()));
        }
        return List.of();
    }

    @Override
    public void addAmmo(int amount) {
        // El math.min asegura que no se pase de la munición máxima
        this.actualMunition = Math.min(this.actualMunition + amount, this.maxMunition);
    }

    @Override
    public void refillAmmo() {
        this.actualMunition = this.maxMunition;
    }

    public int getActualMunition() {
        return actualMunition;
    }

    public int getMaxMunition() {
        return maxMunition;
    }
}
