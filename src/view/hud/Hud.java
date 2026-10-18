package view.hud;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Compone todos los elementos del HUD y los dibuja en orden.
 * Es la única clase del paquete que usa GamePanel.
 */
public class Hud {

    // El orden importa: lo primero queda debajo (viñeta), lo último arriba (avisos).
    private final List<HudElement> elements = List.of(
            new ScreenFlash(),
            new HealthBar(),
            new ShieldBar(),
            new DamageBoostBadge(),
            new AmmoPanel(),
            new PickupToasts());

    private long lastNanos = System.nanoTime();

    /** Avisa a todos los elementos que se agarró un power up (recibe el nombre de la clase del modelo). */
    public void onPickup(String powerUpClassName) {
        PowerUpStyle style = PowerUpStyle.fromClassName(powerUpClassName);
        if (style == null) return;
        for (HudElement e : elements) e.onPickup(style);
    }

    public void draw(Graphics2D g, HudData data, int screenW, int screenH) {
        long now = System.nanoTime();
        double dt = Math.min(0.1, (now - lastNanos) / 1e9);
        lastNanos = now;

        for (HudElement e : elements) {
            e.update(data, dt);

            Graphics2D eg = (Graphics2D) g.create(); // cada elemento con su propio estado gráfico
            try {
                eg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                eg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                e.draw(eg, screenW, screenH);
            } finally {
                eg.dispose();
            }
        }
    }
}
