package view.hud;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

/**
  Barra animada genérica (la usan la vida y el escudo).
  Se encarga de lo común: valor suavizado, estela blanca del daño recibido, flash y temblor.
  Los hijos solo definen colores, icono y qué hacer al perder/ganar.
 */
abstract class StatBar extends HudElement {

    protected double shown;   // valor que se ve (persigue al real con suavizado)
    protected double ghost;   // estela: se queda en el valor viejo y baja después de una pausa
    protected double target;  // valor real
    protected double max = 1;
    protected double flash;   // 0..1, destello sobre la barra
    protected Color flashColor = Color.WHITE;
    protected double shake;   // 0..1, temblor
    protected double pop;     // 0..1, "salto" del icono
    protected double time;

    private double ghostDelay;
    private boolean started;

    protected void track(int value, int maxValue, double dt) {
        time += dt;
        max = Math.max(1, maxValue);
        if (!started) {
            shown = ghost = target = value;
            started = true;
        }
        if (value < target) {
            ghostDelay = 0.5;
            onLoss(target - value);
        } else if (value > target) {
            onGain(value - target);
        }
        target = value;

        shown += (target - shown) * Math.min(1, dt * 10);
        if (Math.abs(target - shown) < 0.05) shown = target;

        if (shown >= ghost) {
            ghost = shown;
        } else if (ghostDelay > 0) {
            ghostDelay -= dt;
        } else {
            ghost += (shown - ghost) * Math.min(1, dt * 4);
        }

        flash = Math.max(0, flash - dt * 2.2);
        shake = Math.max(0, shake - dt * 3);
        pop = Math.max(0, pop - dt * 2.5);
    }

    protected void onLoss(double amount) {
    }

    protected void onGain(double amount) {
    }

    protected void triggerFlash(Color color) {
        flashColor = color;
        flash = 1;
    }

    protected int shakeOffset() {
        return shake > 0 ? (int) (Math.sin(time * 70) * 5 * shake) : 0;
    }

    protected void drawBar(Graphics2D g, int x, int y, int w, int h, Color left, Color right, int segments) {
        RoundRectangle2D shape = new RoundRectangle2D.Double(x, y, w, h, h, h);
        g.setColor(new Color(0, 0, 0, 160));
        g.fill(shape);

        Shape oldClip = g.getClip();
        g.clip(shape);

        // estela del daño
        g.setColor(new Color(255, 255, 255, 150));
        g.fillRect(x, y, (int) (w * clamp01(ghost / max)), h);

        // relleno con degradé, brillo superior y punta luminosa
        int fillW = (int) (w * clamp01(shown / max));
        if (fillW > 0) {
            g.setPaint(new GradientPaint(x, y, left, x + w, y, right));
            g.fillRect(x, y, fillW, h);
            g.setPaint(new GradientPaint(0, y, new Color(255, 255, 255, 95), 0, y + h / 2f, new Color(255, 255, 255, 0)));
            g.fillRect(x, y, fillW, h / 2);
            g.setColor(new Color(255, 255, 255, 170));
            g.fillRect(x + fillW - 2, y, 2, h);
        }

        // divisiones
        if (segments > 1) {
            g.setColor(new Color(0, 0, 0, 75));
            for (int i = 1; i < segments; i++) {
                g.fillRect(x + w * i / segments - 1, y, 2, h);
            }
        }

        if (flash > 0) {
            g.setColor(alpha(flashColor, flash * 0.65));
            g.fill(shape);
        }
        g.setClip(oldClip);

        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(255, 255, 255, 80));
        g.draw(shape);
    }
}
