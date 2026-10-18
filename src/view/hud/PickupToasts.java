package view.hud;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/** Avisos que bajan desde arriba al agarrar un power up ("ESCUDO - Absorbe el daño recibido"). */
public class PickupToasts extends HudElement {

    private static final double LIFE = 2.6;   // segundos en pantalla
    private static final int MAX_VISIBLE = 4;
    private static final int W = 310;
    private static final int H = 58;

    private static class Toast {
        final PowerUpStyle style;
        double age;

        Toast(PowerUpStyle style) {
            this.style = style;
        }
    }

    private final List<Toast> toasts = new ArrayList<>();

    @Override
    public void onPickup(PowerUpStyle style) {
        toasts.add(0, new Toast(style));
        if (toasts.size() > MAX_VISIBLE) toasts.remove(toasts.size() - 1);
    }

    @Override
    public void update(HudData data, double dt) {
        for (Toast t : toasts) t.age += dt;
        toasts.removeIf(t -> t.age > LIFE);
    }

    @Override
    public void draw(Graphics2D g, int screenW, int screenH) {
        for (int i = 0; i < toasts.size(); i++) {
            drawToast(g, toasts.get(i), (screenW - W) / 2, MARGIN + i * (H + 8));
        }
    }

    private void drawToast(Graphics2D g, Toast t, int x, int baseY) {
        double in = clamp01(t.age / 0.4);
        double out = t.age > LIFE - 0.4 ? clamp01((LIFE - t.age) / 0.4) : 1;
        double alphaAll = Math.min(in * 1.5, 1) * out;
        int y = baseY - (int) ((1 - easeOutBack(in)) * 50);

        Color c = t.style.color();
        AffineTransform old = g.getTransform();
        g.translate(x, y);

        RoundRectangle2D box = new RoundRectangle2D.Double(0, 0, W, H, 16, 16);
        g.setColor(new Color(10, 10, 14, (int) (205 * alphaAll)));
        g.fill(box);

        // franja de color a la izquierda
        Shape oldClip = g.getClip();
        g.clip(box);
        g.setColor(alpha(c, alphaAll));
        g.fillRect(0, 0, 6, H);
        g.setClip(oldClip);

        g.setStroke(new BasicStroke(1.5f));
        g.setColor(alpha(c, 0.55 * alphaAll));
        g.draw(box);

        // icono dentro de un círculo + onda expansiva al aparecer
        double cx = 36, cy = H / 2.0;
        if (t.age < 0.55) {
            double k = t.age / 0.55;
            double r = 20 + k * 40;
            g.setStroke(new BasicStroke(3f));
            g.setColor(alpha(c, (1 - k) * 0.8));
            g.draw(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        }
        g.setColor(alpha(c, 0.22 * alphaAll));
        g.fill(new Ellipse2D.Double(cx - 22, cy - 22, 44, 44));
        g.setStroke(new BasicStroke(2f));
        g.setColor(alpha(c, alphaAll));
        g.draw(new Ellipse2D.Double(cx - 22, cy - 22, 44, 44));

        AffineTransform inner = g.getTransform();
        g.translate(cx, cy);
        g.setColor(alpha(c, alphaAll));
        t.style.drawIcon(g, 12);
        g.setTransform(inner);

        g.setFont(FONT.deriveFont(Font.BOLD, 17f));
        shadowText(g, t.style.title(), 72, 26, alpha(Color.WHITE, alphaAll));
        g.setFont(FONT.deriveFont(Font.PLAIN, 12.5f));
        shadowText(g, t.style.subtitle(), 72, 45, alpha(new Color(200, 200, 205), alphaAll));

        g.setTransform(old);
    }
}
