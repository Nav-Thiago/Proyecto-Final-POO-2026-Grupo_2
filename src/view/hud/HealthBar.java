package view.hud;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/** Barra de vida: cambia de color según la salud, late cuando queda poca y reacciona a daño y curas. */
public class HealthBar extends StatBar {

    private static final int BAR_W = 300;
    private static final int BAR_H = 24;
    private static final Color RED = new Color(215, 35, 45);
    private static final Color YELLOW = new Color(240, 190, 50);
    private static final Color GREEN = new Color(70, 205, 95);

    @Override
    public void update(HudData data, double dt) {
        track(data.health(), data.maxHealth(), dt);
    }

    @Override
    protected void onLoss(double amount) {
        triggerFlash(Color.RED);
        shake = 1;
    }

    @Override
    public void onPickup(PowerUpStyle style) {
        if (style == PowerUpStyle.HEAL) {
            triggerFlash(style.color());
            pop = 1;
        }
    }

    @Override
    public void draw(Graphics2D g, int screenW, int screenH) {
        double ratio = clamp01(shown / max);
        boolean low = ratio < 0.3 && target > 0;
        double beat = low ? Math.max(0, Math.sin(time * (7 + (0.3 - ratio) * 25))) : 0;

        Color base = ratio < 0.5 ? mix(RED, YELLOW, ratio / 0.5) : mix(YELLOW, GREEN, (ratio - 0.5) / 0.5);
        Color left = mix(base, Color.BLACK, 0.15);
        Color right = mix(base, Color.WHITE, 0.25);

        int x = MARGIN + shakeOffset();
        int y = MARGIN;

        double scale = 1 + pop * 0.35 + beat * 0.2 + (low ? 0 : 0.03 * Math.sin(time * 3));
        drawHeart(g, x + 18, y + BAR_H / 2.0, scale, base, beat);

        int bx = x + 44;
        drawBar(g, bx, y, BAR_W, BAR_H, left, right, 1);

        g.setFont(FONT);
        shadowText(g, "VIDA", bx + 14, y + 17, new Color(255, 255, 255, 230));
        String value = Math.round(shown) + " / " + (int) max;
        shadowText(g, value, bx + BAR_W - 14 - textWidth(g, value), y + 17, Color.WHITE);
    }

    private void drawHeart(Graphics2D g, double cx, double cy, double scale, Color c, double beat) {
        AffineTransform old = g.getTransform();
        g.translate(cx, cy);
        g.scale(scale, scale);

        Path2D.Double heart = new Path2D.Double();
        heart.moveTo(0, 12);
        heart.curveTo(-21, -2, -11, -17, 0, -7);
        heart.curveTo(11, -17, 21, -2, 0, 12);
        heart.closePath();

        g.setStroke(new BasicStroke(8f));
        g.setColor(alpha(c, 0.25 + 0.25 * beat + flash * 0.3));
        g.draw(heart);
        g.setPaint(new GradientPaint(0, -14, mix(c, Color.WHITE, 0.4), 0, 12, c));
        g.fill(heart);
        g.setColor(new Color(255, 255, 255, 140));
        g.fill(new Ellipse2D.Double(-10, -11, 6, 4));

        g.setTransform(old);
    }
}
