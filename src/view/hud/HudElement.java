package view.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
  Clase base de todo lo que se dibuja en el HUD.
  El Hud los trata a todos igual (update + draw); cada hijo decide cómo se ve y cómo reacciona.
 */
public abstract class HudElement {

    protected static final int MARGIN = 24;
    protected static final Font FONT = new Font("SansSerif", Font.BOLD, 14);

    // Se llama una vez por frame con los datos actuales y el tiempo transcurrido en segundos. 
    public void update(HudData data, double dt) {
    }

    // Se llama cuando el jugador agarra un power up, para que el elemento reaccione. 
    public void onPickup(PowerUpStyle style) {
    }

    public abstract void draw(Graphics2D g, int screenW, int screenH);

    // ---------- utilidades compartidas por los hijos ----------

    protected static double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
    }

    protected static Color alpha(Color c, double a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (clamp01(a) * 255));
    }

    protected static Color mix(Color a, Color b, double t) {
        t = clamp01(t);
        return new Color(
                (int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    protected static double easeOutBack(double t) {
        double c1 = 1.70158, c3 = c1 + 1;
        return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
    }

    protected static void shadowText(Graphics2D g, String s, float x, float y, Color c) {
        g.setColor(new Color(0, 0, 0, 190));
        g.drawString(s, x + 1.5f, y + 1.5f);
        g.setColor(c);
        g.drawString(s, x, y);
    }

    protected static int textWidth(Graphics2D g, String s) {
        return g.getFontMetrics().stringWidth(s);
    }
}
