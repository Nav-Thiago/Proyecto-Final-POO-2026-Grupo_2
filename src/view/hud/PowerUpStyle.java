package view.hud;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

/**
  Identidad visual de cada power up: color, textos del aviso e icono vectorial.
  Cada constante dibuja su propio icono (polimorfismo), así el HUD no necesita ningún if/switch por tipo.
 */
public enum PowerUpStyle {

    HEAL("Heal", new Color(80, 225, 120), "VIDA", "Salud restaurada") {
        @Override
        public void drawIcon(Graphics2D g, int r) {
            int t = Math.max(3, r / 2);
            g.fillRoundRect(-t / 2, -r, t, r * 2, 3, 3);
            g.fillRoundRect(-r, -t / 2, r * 2, t, 3, 3);
        }
    },

    SHIELD("Shield", new Color(70, 200, 255), "ESCUDO", "Absorbe el daño recibido") {
        @Override
        public void drawIcon(Graphics2D g, int r) {
            double s = r / 12.0;
            Path2D.Double p = new Path2D.Double();
            p.moveTo(0, -12 * s);
            p.lineTo(11 * s, -7 * s);
            p.lineTo(10 * s, 3 * s);
            p.curveTo(8 * s, 9 * s, 3 * s, 12 * s, 0, 14 * s);
            p.curveTo(-3 * s, 12 * s, -8 * s, 9 * s, -10 * s, 3 * s);
            p.lineTo(-11 * s, -7 * s);
            p.closePath();
            g.fill(p);
        }
    },

    DAMAGE_BOOST("DamageBoost", new Color(255, 85, 70), "DAÑO +", "Tus balas pegan más fuerte") {
        @Override
        public void drawIcon(Graphics2D g, int r) {
            g.setStroke(new BasicStroke(Math.max(2f, r / 3.5f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < 2; i++) {
                double oy = -r * 0.35 + i * r * 0.75;
                Path2D.Double p = new Path2D.Double();
                p.moveTo(-r * 0.8, oy + r * 0.35);
                p.lineTo(0, oy - r * 0.35);
                p.lineTo(r * 0.8, oy + r * 0.35);
                g.draw(p);
            }
        }
    },

    AMMO("Ammo", new Color(255, 215, 60), "MUNICIÓN", "Balas recargadas") {
        @Override
        public void drawIcon(Graphics2D g, int r) {
            bullet(g, -r * 0.45, 0, r * 1.8);
            bullet(g, r * 0.45, 0, r * 1.8);
        }
    },

    MAX_AMMO("MaxAmmo", new Color(255, 150, 40), "CARGADOR LLENO", "Munición al máximo") {
        @Override
        public void drawIcon(Graphics2D g, int r) {
            bullet(g, -r * 0.8, r * 0.1, r * 1.5);
            bullet(g, 0, 0, r * 2.0);
            bullet(g, r * 0.8, r * 0.1, r * 1.5);
        }
    };

    private final String className;
    private final Color color;
    private final String title;
    private final String subtitle;

    PowerUpStyle(String className, Color color, String title, String subtitle) {
        this.className = className;
        this.color = color;
        this.title = title;
        this.subtitle = subtitle;
    }

    public Color color() {
        return color;
    }

    public String title() {
        return title;
    }

    public String subtitle() {
        return subtitle;
    }

    /** Dibuja el icono centrado en (0,0) con el color y el stroke que tenga el Graphics2D. */
    public abstract void drawIcon(Graphics2D g, int radius);

    /** Traduce el nombre de la clase del modelo (Heal, Shield...) a su estilo; null si no se conoce. */
    public static PowerUpStyle fromClassName(String name) {
        for (PowerUpStyle s : values()) {
            if (s.className.equals(name)) return s;
        }
        return null;
    }

    private static void bullet(Graphics2D g, double cx, double cy, double h) {
        double w = h * 0.2;
        Path2D.Double p = new Path2D.Double();
        p.moveTo(cx - w, cy + h / 2);
        p.lineTo(cx - w, cy - h * 0.1);
        p.curveTo(cx - w, cy - h * 0.4, cx - w * 0.5, cy - h / 2, cx, cy - h / 2);
        p.curveTo(cx + w * 0.5, cy - h / 2, cx + w, cy - h * 0.4, cx + w, cy - h * 0.1);
        p.lineTo(cx + w, cy + h / 2);
        p.closePath();
        g.fill(p);
    }
}
