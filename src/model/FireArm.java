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

    public void attack(double x, double y, double angle, List<Bullet> bullets, List<Enemy> enemies){
        long currentTime = System.currentTimeMillis();

        if (this.actualMunition > 0 && currentTime - this.lastShoot >= this.cooldown) {

            this.actualMunition -= 1;
            this.lastShoot = currentTime;

            double dirX = Math.cos(angle);
            double dirY = Math.sin(angle);
        }
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
