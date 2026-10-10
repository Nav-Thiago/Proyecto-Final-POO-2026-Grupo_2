package model;

public class Handgun extends FireArm {

    public Handgun(int damage, long cooldown, int actualMunition, int maxMunition) {
        super(damage, cooldown, actualMunition, maxMunition);
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

}
