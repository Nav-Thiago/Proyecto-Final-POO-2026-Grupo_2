package view.hud;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

/** Etiqueta "DAÑO +25" que pulsa mientras el boost de daño está activo. */
public class DamageBoostBadge extends HudElement {

    private static final int W = 150;
    private static final int H = 26;

    private double bonus;
    private double appear;
    private double pop;
    private double time;

    @Override
    public void update(HudData data, double dt) {
        time += dt;
        bonus = data.damageBonus();
        appear += ((bonus > 0 ? 1 : 0) - appear) * Math.min(1, dt * 8);
        pop = Math.max(0, pop - dt * 2.5);
    }

    @Override
    public void onPickup(PowerUpStyle style) {
        if (style == PowerUpStyle.DAMAGE_BOOST) pop = 1;
    }

    @Override
    public void draw(Graphics2D g, int screenW, int screenH) {
        if (appear < 0.02) return;

        Color c = PowerUpStyle.DAMAGE_BOOST.color();
        double pulse = 0.5 + 0.5 * Math.sin(time * 6);
        int x = MARGIN + 44;
        int y = MARGIN + 38 + 18 + 10;

        g.setComposite(AlphaComposite.SrcOver.derive((float) clamp01(appear)));
        AffineTransform old = g.getTransform();
        g.translate(x + W / 2.0, y + H / 2.0);
        g.scale(1 + pop * 0.25, 1 + pop * 0.25);
        g.translate(-W / 2.0, -H / 2.0);

        RoundRectangle2D pill = new RoundRectangle2D.Double(0, 0, W, H, H, H);
        g.setColor(alpha(c, 0.18 + 0.15 * pulse));
        g.fill(new RoundRectangle2D.Double(-4, -4, W + 8, H + 8, H + 8, H + 8));
        g.setColor(new Color(20, 5, 5, 200));
        g.fill(pill);
        g.setColor(alpha(c, 0.7 + 0.3 * pulse));
        g.setStroke(new BasicStroke(1.8f));
        g.draw(pill);

        AffineTransform inner = g.getTransform();
        g.translate(18, H / 2.0);
        g.setColor(c);
        PowerUpStyle.DAMAGE_BOOST.drawIcon(g, 8);
        g.setTransform(inner);

        g.setFont(FONT);
        shadowText(g, "DAÑO +" + Math.round(bonus), 36, 18, Color.WHITE);
        g.setTransform(old);
    }
}
