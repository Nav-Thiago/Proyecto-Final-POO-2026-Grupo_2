package view.hud;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/** Barra de escudo: aparece cuando hay escudo, se desvanece al agotarse y destella al absorber golpes. */
public class ShieldBar extends StatBar {

    private static final int BAR_W = 300;
    private static final int BAR_H = 18;
    private static final Color LEFT = new Color(35, 140, 225);
    private static final Color RIGHT = new Color(130, 240, 255);

    private double appear; // 0 oculto .. 1 visible

    @Override
    public void update(HudData data, double dt) {
        track(data.shield(), data.maxShield(), dt);
        double want = (target > 0 || shown > 0.5) ? 1 : 0;
        appear += (want - appear) * Math.min(1, dt * 8);
    }

    @Override
    protected void onLoss(double amount) {
        triggerFlash(Color.WHITE);
        shake = 1;
    }

    @Override
    public void onPickup(PowerUpStyle style) {
        if (style == PowerUpStyle.SHIELD) {
            triggerFlash(style.color());
            pop = 1;
        }
    }

    @Override
    public void draw(Graphics2D g, int screenW, int screenH) {
        if (appear < 0.02) return;

        g.setComposite(AlphaComposite.SrcOver.derive((float) clamp01(appear)));
        int x = MARGIN + shakeOffset();
        int y = MARGIN + 38 + (int) ((1 - appear) * -10);

        // icono con aura
        AffineTransform old = g.getTransform();
        g.translate(x + 18, y + BAR_H / 2.0);
        double s = 1 + pop * 0.4 + 0.03 * Math.sin(time * 3);
        g.scale(s * 1.35, s * 1.35);
        g.setColor(alpha(PowerUpStyle.SHIELD.color(), 0.28 + flash * 0.4));
        PowerUpStyle.SHIELD.drawIcon(g, 12);
        g.setTransform(old);
        g.translate(x + 18, y + BAR_H / 2.0);
        g.scale(s, s);
        g.setColor(mix(PowerUpStyle.SHIELD.color(), Color.WHITE, 0.35));
        PowerUpStyle.SHIELD.drawIcon(g, 11);
        g.setTransform(old);

        int bx = x + 44;
        drawBar(g, bx, y, BAR_W, BAR_H, LEFT, RIGHT, Math.max(1, (int) (max / 10)));

        g.setFont(FONT.deriveFont(12f));
        shadowText(g, "ESCUDO", bx + 14, y + 14, new Color(255, 255, 255, 230));
        String value = String.valueOf(Math.round(shown));
        shadowText(g, value, bx + BAR_W - 14 - textWidth(g, value), y + 14, Color.WHITE);
    }
}
