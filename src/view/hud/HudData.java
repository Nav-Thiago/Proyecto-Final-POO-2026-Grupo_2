package view.hud;

/** Foto inmutable de lo que el HUD necesita mostrar en este frame. */
public final class HudData {

    private final int health;
    private final int maxHealth;
    private final int shield;
    private final int maxShield;
    private final double damageBonus;
    private final boolean ranged;
    private final String weaponName;
    private final int ammo;
    private final int maxAmmo;

    public HudData(int health, int maxHealth, int shield, int maxShield,
                   double damageBonus, boolean ranged, String weaponName,
                   int ammo, int maxAmmo) {
        this.health = health;
        this.maxHealth = maxHealth;
        this.shield = shield;
        this.maxShield = maxShield;
        this.damageBonus = damageBonus;
        this.ranged = ranged;
        this.weaponName = weaponName;
        this.ammo = ammo;
        this.maxAmmo = maxAmmo;
    }

    public int health() { return health; }
    public int maxHealth() { return maxHealth; }
    public int shield() { return shield; }
    public int maxShield() { return maxShield; }
    public double damageBonus() { return damageBonus; }
    public boolean ranged() { return ranged; }
    public String weaponName() { return weaponName; }
    public int ammo() { return ammo; }
    public int maxAmmo() { return maxAmmo; }
}
