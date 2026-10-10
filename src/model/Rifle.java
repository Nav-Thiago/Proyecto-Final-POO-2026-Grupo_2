package model;

public class Rifle extends FireArm {

    public Rifle(int damage, long cooldown, int actualMunition, int maxMunition) {
        super(damage, cooldown, actualMunition, maxMunition);
    }

    @Override
    public boolean isAutomatic() {
        return true;
    }

}
