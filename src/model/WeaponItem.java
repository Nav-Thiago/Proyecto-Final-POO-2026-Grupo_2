package model;

public class WeaponItem extends Entity {

    private final FireArm weapon;

    public WeaponItem(double x, double y, FireArm weapon) {
        super(x, y, 32, 32);
        this.weapon = weapon;
    }

    public FireArm getWeapon() {
        return weapon;
    }
}
