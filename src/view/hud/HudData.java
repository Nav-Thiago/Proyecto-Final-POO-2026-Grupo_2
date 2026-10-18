package view.hud;

/** Foto inmutable de lo que el HUD necesita mostrar en este frame (la arma el controlador a partir del modelo). */
public record HudData(
        int health, int maxHealth,
        int shield, int maxShield,
        double damageBonus,
        boolean ranged, String weaponName, int ammo, int maxAmmo) {
}
