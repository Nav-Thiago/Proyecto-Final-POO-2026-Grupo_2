package view.hud;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

/** Panel inferior derecho: arma equipada, balas y cargador visual. Destella al agarrar munición. */
public class AmmoPanel extends HudElement {

    private static final int W = 250;
    private static final int H = 92;
    private static final Color YELLOW = new Color(255, 210, 70);

    private HudData data;
    private String lastWeapon = "";
    private int lastAmmo = -1;
    private double pop;
    private double flash;
    private double time;

    @Override
    public void update(HudData d, double dt) {
        time += dt;
        data = d;
        if (!d.weaponName().equals(lastWeapon)) {
            lastWeapon = d.weaponName();
            lastAmmo = -1;
        }
        if (lastAmmo >= 0 && d.ammo() != lastAmmo) {
            pop = d.ammo() > lastAmmo ? 1 : 0.45;
        }
        lastAmmo = d.ammo();
        pop = Math.max(0, pop - dt * 4);
        flash = Math.max(0, flash - dt * 2.2);
    }

    @Override
    public void onPickup(PowerUpStyle style) {
        if (style == PowerUpStyle.AMMO || style == PowerUpStyle.MAX_AMMO) {
            flash = 1;
            pop = 1;
        }
    }

    @Override
    public void draw(Graphics2D g, int screenW, int screenH) {
        if (data == null) return;

        int x = screenW - MARGIN - W;
        int y = screenH - MARGIN - H;

        RoundRectangle2D box = new RoundRectangle2D.Double(x, y, W, H, 18, 18);
        g.setColor(new Color(0, 0, 0, 160));
        g.fill(box);
        if (flash > 0) {
            g.setColor(alpha(YELLOW, flash * 0.45));
            g.fill(box);
        }
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(255, 255, 255, 80));
        g.draw(box);

        g.setFont(FONT.deriveFont(12f));
        shadowText(g, weaponLabel(data.weaponName()), x + 16, y + 22, new Color(200, 200, 200));

        if (!data.ranged()) {
            g.setFont(FONT.deriveFont(Font.BOLD, 20f));
            shadowText(g, "CUERPO A CUERPO", x + 16, y + 62, Color.WHITE);
            return;
        }

        boolean empty = data.ammo() <= 0;
        boolean low = data.ammo() <= data.maxAmmo() * 0.2;
        boolean blink = Math.sin(time * 10) > 0;
        Color numColor = low && (blink || !empty) ? new Color(255, 70, 60) : Color.WHITE;
        if (flash > 0) numColor = mix(numColor, YELLOW, flash);

        // número grande con "salto" al cambiar
        String ammo = String.valueOf(data.ammo());
        AffineTransform old = g.getTransform();
        g.translate(x + 16, y + 60);
        g.scale(1 + pop * 0.25, 1 + pop * 0.25);
        g.setFont(FONT.deriveFont(Font.BOLD, 38f));
        shadowText(g, ammo, 0, 0, numColor);
        int numW = textWidth(g, ammo);
        g.setTransform(old);

        g.setFont(FONT.deriveFont(Font.BOLD, 18f));
        shadowText(g, "/ " + data.maxAmmo(), x + 16 + numW + 8, y + 60, new Color(170, 170, 170));

        if (empty && blink) {
            g.setFont(FONT.deriveFont(12f));
            String msg = "SIN BALAS";
            shadowText(g, msg, x + W - 16 - textWidth(g, msg), y + 22, new Color(255, 70, 60));
        }

        // cargador: una barrita por bala (máx. 30)
        int count = Math.min(data.maxAmmo(), 30);
        if (count > 0) {
            int filled = (int) Math.ceil(count * (double) data.ammo() / data.maxAmmo());
            int inner = W - 32;
            int pipW = Math.max(2, (inner - (count - 1) * 2) / count);
            for (int i = 0; i < count; i++) {
                g.setColor(i < filled ? (low ? new Color(255, 80, 70) : YELLOW) : new Color(255, 255, 255, 40));
                g.fillRoundRect(x + 16 + i * (pipW + 2), y + H - 20, pipW, 9, 3, 3);
            }
        }
    }

    private static String weaponLabel(String className) {
        return switch (className) {
            case "Handgun" -> "PISTOLA";
            case "Rifle" -> "RIFLE";
            case "Shotgun" -> "ESCOPETA";
            case "MeleeWeapon" -> "CUCHILLO";
            default -> className.toUpperCase();
        };
    }
}
