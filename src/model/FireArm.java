package model;

public class FireArm extends Weapon implements AmmoHolder {

    protected int actualMunition;
    private final int maxMunition;

    public FireArm(int damage, long cooldown, int actualMunition, int maxMunition){
        super(damage);
        this.cooldown = cooldown;
        this.actualMunition = actualMunition;
        this.maxMunition = maxMunition;
    }

    @Override
    public boolean attack(Player player, GameState state){
        long currentTime = System.currentTimeMillis();

        if (this.actualMunition > 0 && currentTime - this.lastShoot >= this.cooldown) {

            this.actualMunition--;
            this.lastShoot = currentTime;

            double dirX = Math.cos(player.getAngle());
            double dirY = Math.sin(player.getAngle());

            double centerX = player.getX() + (player.getWidth() / 2.0);
            double centerY = player.getY() + (player.getHeight() / 2.0);

            double totalDamage = this.getDamage() + player.getDamageBonus();
            Bullet bullet = new Bullet(centerX, centerY, 6, 6, dirX, dirY, totalDamage, 60);

            state.addPlayerBullet(bullet);

            return true;
        }
        return false;
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
