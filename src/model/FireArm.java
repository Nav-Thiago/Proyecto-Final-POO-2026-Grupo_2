package model;

import java.util.List;

public class FireArm extends Weapon {

    public FireArm(double x, double y, int width, int height, int damage){
        super(x, y, width, height, damage);
    }

    @Override
    public List<Bullet> shoot(double x, double y, double angle) {
        // Punto desde donde sale la bala
        double origenX = this.x + (this.width / 2.0);
        double origenY = this.y + (this.height / 2.0);

        double dirX = Math.cos(angle);
        double dirY = Math.sin(angle);

        return List.of(origenX, origenY, dirX, dirY, 12.0, this.getDamage());
    }
}
