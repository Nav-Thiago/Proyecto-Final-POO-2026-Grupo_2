package model;

import java.util.*;
import java.util.function.UnaryOperator;

public class Player extends Character {

    private static final int MAX_HEALTH = 100;
    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;
    private boolean isAttacking;

    // [POWERUP] estado que modifican los power ups
    private final List<Effect> effects = new ArrayList<>();
    private PlayerStats stats = new BaseStats(); // cadena de decoradores (escudo, daño)

    private double angle; // Ángulo en radianes hacia el cursor

    private final MeleeWeapon knife;
    private FireArm primaryWeapon; // Arma de fuego equipada
    private Weapon activeWeapon; // Puntero al arma que tiene en la mano actualmente

    // Variables de inmunidad
    private long lastDamageTime = 0;
    private final int invulnerabilityCooldown = 1000; // 1 segundo de inmunidad al chocar
     
    //variable para llevar un registro del daño total recibido por el jugador(HUD)
    private int damageTaken = 0;

    private final Queue<String> pickupEvents = new LinkedList<>();

    private final GameClock clock;

    public Player(double x, double y, GameClock clock) {
        super(x, y, 64, 64, 12 , MAX_HEALTH);

        this.clock = clock;
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

    public void setAttacking(boolean attacking) {
        this.isAttacking = attacking;
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

    public GameClock getClock() {
        return clock;
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    public void update(GameState state) {
        updateEffects(); // vence los efectos temporales
        stats = stats.prune(); // descarta los decoradores vencidos

        if (this.isAttacking && this.activeWeapon != null && this.activeWeapon.isAutomatic()) {
            this.attack(state);
        }

        double dx = 0;
        double dy = 0;
        if (moveUp) dy -= 1;
        if (moveDown) dy += 1;
        if (moveLeft) dx -= 1;
        if (moveRight) dx += 1;

        move(dx, dy, state);

        // Bordes del mapa calculados 
        if (x < 0) x = 0;
        if (x + width > state.getMapWidth()) x = state.getMapWidth() - width;
        if (y < 0) y = 0;
        if (y + height > state.getMapHeight()) y = state.getMapHeight() - height;
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
        } else {
            this.activeWeapon = this.knife;
        }
    }

    public void equipWeapon(FireArm weapon) {
        this.primaryWeapon = weapon;
        this.activeWeapon = this.primaryWeapon;
    }

    public void attack(GameState state) {
        if (this.activeWeapon != null) {
            this.activeWeapon.attack(this, state);
        }
    }

    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    private void updateEffects() {
        Iterator<Effect> it = effects.iterator();
        while (it.hasNext()) {
            Effect e = it.next();
            e.update(this);
            if (e.isExpired()) {
                it.remove();
            }
        }
    }

    // Limpia las teclas para utilizarlo en el menú de pausa y que el personaje no siga caminando
    public void clearInputs() {
        moveUp = false;
        moveDown = false;
        moveLeft = false;
        moveRight = false;
        isAttacking = false;
    }

    @Override
    public void takeDamage(int amount) {
        long currentTime = clock.nowMillis();
        
        // Evaluamos el cooldown de choque antes de procesar el daño
        if (currentTime - lastDamageTime >= invulnerabilityCooldown) {
            // Los decoradores de escudo absorben primero; a la vida llega lo que sobra
            int reachesHealth = stats.absorbDamage(amount);
            int absorbedByShield = amount - reachesHealth;

            int healthBefore = getHealth();
            super.takeDamage(reachesHealth);

            damageTaken += absorbedByShield + (healthBefore - getHealth());

            // Reiniciamos el temporizador de inmunidad
            this.lastDamageTime = currentTime;
        }
    }

    // Envuelve las stats actuales con un nuevo decorador
    public void decorateStats(UnaryOperator<PlayerStats> decorator) {
        stats = decorator.apply(stats);
    }

    public double getDamageBonus() {
        return stats.getDamageBonus();
    }

    public void heal(int amount) {
        setHealth(Math.min(getHealth() + amount, MAX_HEALTH));
    }

    public int getShield() {
        return stats.getShield();
    }

    public void addPickupAmmo() {
        if (primaryWeapon != null)
            primaryWeapon.addAmmo(primaryWeapon.getAmmoPerPickup());
    }

    public void refillAmmo() {
        if (primaryWeapon != null) {
            primaryWeapon.refillAmmo();
        }
    }

    public int getDamageTaken() {
        return damageTaken;
    }

    public void notifyPickup(String powerUpType) {
        pickupEvents.add(powerUpType);
    }

    public String pollPickup() {
        return pickupEvents.poll();
    }
}