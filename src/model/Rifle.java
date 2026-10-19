package model;

public class Rifle extends FireArm {

    public Rifle(int damage, long cooldown, int actualMunition, int maxMunition) {
        super(damage, cooldown, actualMunition, maxMunition);
    }

    @Override
    public boolean isAutomatic() {
        return true;
    }

    @Override
    public int getAmmoPerPickup() {
    return 10;
    } //recibe 10 balas por cada power up de munición
}
