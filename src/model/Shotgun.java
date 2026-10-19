package model;
 
import java.util.Random;
 
public class Shotgun extends FireArm {
 
    private final Random random = new Random();
 
    public Shotgun(int damage, int cooldown, int actualMunition, int maxMunition) {
        super(damage, cooldown, actualMunition, maxMunition);
    }
 
    @Override
    public boolean attack(Player player, GameState state) {
        long currentTime = System.currentTimeMillis();
 
        if (this.actualMunition > 0 && currentTime - this.lastShoot >= this.cooldown) {
 
            this.actualMunition--;
            this.lastShoot = currentTime;
 
            double totalDamage = this.getDamage() + player.getDamageBonus();

            for (int i = 0; i < 4; i++) {
                // random.nextDouble() da un valor entre 0.0 y 1.0.
                // Al restar 0.5 queda entre -0.5 y 0.5.
                // Multiplicado por 0.6 genera una dispersión máxima de +/- 0.3 radianes.
                double offset = (random.nextDouble() - 0.5) * 0.6;
                double spreadAngle = player.getAngle() + offset;
 
                double dirX = Math.cos(spreadAngle);
                double dirY = Math.sin(spreadAngle);
 
                double muzzleX = getMuzzleX(player);
                double muzzleY = getMuzzleY(player);
 
                Bullet pellet = new Bullet(muzzleX, muzzleY, 6, 6, dirX, dirY, totalDamage, 30, BulletKind.PELLET);
                state.addPlayerBullet(pellet);
            }
            return true;
        }
        return false;
    }

    @Override 
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public int getAmmoPerPickup() {
    return 3;
} // recibe 3 balas por cada power up de munición
}
