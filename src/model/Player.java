package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player extends Character {

    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;

    // [POWERUP] estado que modifican los power ups
    private final List<Effect> effects = new ArrayList<>();
    private double damageBonus = 0; // daño extra que se suma a cada bala
    private int shield = 0;
    private AmmoHolder ammoHolder; // arma actual (puede ser null hasta que exista Weapon)

    private double angle; // Ángulo en radianes hacia el cursor

    private final MeleeWeapon knife;
    private FireArm primaryWeapon; // Arma de fuego equipada
    private Weapon activeWeapon; // Puntero al arma que tiene en la mano actualmente

    public Player(double x, double y) {
        super(x, y, 64, 64, 5.5, 100, 100);

        this.knife = new MeleeWeapon(50, 1000);
        this.activeWeapon = this.knife;
    }

    public void setMoveUp(boolean moveUp) {
        this.moveUp = moveUp;
    }

    public void setMoveDown(boolean moveDown) {
        this.moveDown = moveDown;
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public boolean isMoving() {
        return moveUp || moveDown || moveRight || moveLeft;
    }

    public double getAngle() {
        return angle;
    }

    public FireArm getPrimaryWeapon() {
        return primaryWeapon;
    }

    public Weapon getActiveWeapon() {
        return activeWeapon;
    }

    public void update(int mapWidth, int mapHeight) {
        updateEffects(); // [POWERUP] vence los efectos temporales

        double dx = 0;
        double dy = 0;
        if (moveUp) dy -= 1;
        if (moveDown) dy += 1;
        if (moveLeft) dx -= 1;
        if (moveRight) dx += 1;

        move(dx, dy);

        if (x < 0) x = 0;
        if (x + width > mapWidth) x = mapWidth - width;
        if (y < 0) y = 0;
        if (y + height > mapHeight) y = mapHeight - height;
    }

    public void aimAt(double mouseX, double mouseY) {
        //Centro real del personaje
        double centerX = this.getX() + (this.getWidth() / 2.0);
        double centerY = this.getY() + (this.getHeight() / 2.0);

        double deltaX = mouseX - centerX;
        double deltaY = mouseY - centerY;

        this.angle = Math.atan2(deltaY, deltaX);
    }

    public void swapWeapon() {
        if (this.activeWeapon == this.knife && this.primaryWeapon != null) {
            this.activeWeapon = this.primaryWeapon;
            this.setAmmoHolder(this.primaryWeapon);
        } else {
            this.activeWeapon = this.knife;
            this.setAmmoHolder(null);
        }
    }

    public void equipWeapon(FireArm weapon) {
        this.primaryWeapon = weapon;
        this.activeWeapon = this.primaryWeapon;
        this.setAmmoHolder(weapon);
    }

    public void attack(GameState state) {
        if (this.activeWeapon != null) {
            this.activeWeapon.attack(this, state);
        }
    }

    public void addEffect(Effect effect) {
        effect.apply(this);
        effects.add(effect);
    }

    private void updateEffects() {
        Iterator<Effect> it = effects.iterator();
        while (it.hasNext()) {
            Effect e = it.next();
            e.update(this);
            if (e.isExpired()) {
                e.remove(this);
                it.remove();
            }
        }
    }

    public double getDamageBonus() {
        return damageBonus;
    }

    public void setDamageBonus(double damageBonus) {
        this.damageBonus = damageBonus;
    }

    public void heal(int amount) {
        setHealth(Math.min(getHealth() + amount, getMaxHealth()));
    }

    public void addShield(int points) {
        shield += points;
    }

    public int getShield() {
        return shield;
    }

    @Override
    public void takeDamage(int amount) {
        // el escudo absorbe primero
        int absorbed = Math.min(shield, amount);
        shield -= absorbed;
        super.takeDamage(amount - absorbed);
    }

    public void setAmmoHolder(AmmoHolder ammoHolder) {
        this.ammoHolder = ammoHolder;
    }

    public void addAmmo(int amount) {
        if (ammoHolder != null) ammoHolder.addAmmo(amount);
    }

    public void refillAmmo() {
        if (this.ammoHolder != null) {
            this.ammoHolder.refillAmmo();
        }
    }
}

