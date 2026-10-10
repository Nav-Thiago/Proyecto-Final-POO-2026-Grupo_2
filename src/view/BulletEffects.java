package view;
 
import model.Bullet;
import model.BulletKind;
import model.FireArm;
import model.Player;
import model.ShotGun;
import model.Weapon;
 
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;
 
/**
 * Efectos visuales de disparo: estelas (tracers) de las balas y destello en la boca del arma.
 * Solo dibuja; no modifica el modelo. Hay que llamarlo con el Graphics2D en coordenadas de MUNDO.
 */
public class BulletEffects {
 
    private static final long FLASH_MS = 70;
    private static final long SHOTGUN_FLASH_MS = 100;
 
    // ESTELAS
 
    public void drawTracers(Graphics2D g2, List<Bullet> bullets) {
        Stroke oldStroke = g2.getStroke();
        Paint oldPaint = g2.getPaint();
 
        for (Bullet b : bullets) {
            if (b.isActive()) {
                drawTracer(g2, b);
            }
        }
 
        g2.setStroke(oldStroke);
        g2.setPaint(oldPaint);
    }
 
    private void drawTracer(Graphics2D g2, Bullet b) {
        boolean pellet = b.getKind() == BulletKind.PELLET;
 
        double maxLength = pellet ? 18 : 36;
        // La estela crece desde 0 los primeros ticks: parece que "sale" del arma
        double length = Math.min(maxLength, b.getAge() * b.getSpeed() + 6);
 
        double headX = b.getX() + b.getWidth() / 2.0;
        double headY = b.getY() + b.getHeight() / 2.0;
        double tailX = headX - b.getDirX() * length;
        double tailY = headY - b.getDirY() * length;
 
        Color glow = pellet ? new Color(255, 110, 20) : new Color(255, 185, 60);
        Line2D line = new Line2D.Double(tailX, tailY, headX, headY);
 
        // Halo exterior
        g2.setPaint(new GradientPaint((float) tailX, (float) tailY, alpha(glow, 0),
                (float) headX, (float) headY, alpha(glow, 90)));
        g2.setStroke(new BasicStroke(pellet ? 7f : 9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(line);
 
        // Cuerpo
        g2.setPaint(new GradientPaint((float) tailX, (float) tailY, alpha(glow, 0),
                (float) headX, (float) headY, alpha(glow, 210)));
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(line);
 
        // Núcleo blanco-amarillento
        Color core = new Color(255, 250, 210);
        g2.setPaint(new GradientPaint((float) tailX, (float) tailY, alpha(core, 0),
                (float) headX, (float) headY, alpha(core, 255)));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(line);
 
        // Punta brillante
        g2.setPaint(alpha(core, 235));
        g2.fill(new Ellipse2D.Double(headX - 2.5, headY - 2.5, 5, 5));
    }
 
    // DESTELLO DE BOCA DE FUEGO
 
    public void drawMuzzleFlash(Graphics2D g2, Player player) {
        if (!player.isAlive()) return;
 
        Weapon weapon = player.getActiveWeapon();
        if (!(weapon instanceof FireArm)) return; // el cuchillo no tiene destello
 
        boolean shotgun = weapon instanceof ShotGun;
        long duration = shotgun ? SHOTGUN_FLASH_MS : FLASH_MS;
        long elapsed = System.currentTimeMillis() - weapon.getLastShoot();
        if (elapsed < 0 || elapsed > duration) return;
 
        double life = 1.0 - (double) elapsed / duration; // 1 al disparar -> 0 al apagarse
 
        double centerX = player.getX() + player.getWidth() / 2.0;
        double centerY = player.getY() + player.getHeight() / 2.0;
 
        AffineTransform old = g2.getTransform();
        Paint oldPaint = g2.getPaint();
        Stroke oldStroke = g2.getStroke();
 
        // A partir de acá, el eje X apunta hacia donde mira el jugador y (0,0) es la boca del arma
        g2.translate(centerX, centerY);
        g2.rotate(player.getAngle());
        g2.translate(Weapon.MUZZLE_FORWARD, Weapon.MUZZLE_SIDE);
 
        // Misma semilla durante todo el destello: la forma se mantiene y solo se desvanece
        Random rnd = new Random(weapon.getLastShoot());
 
        drawFlashGlow(g2, life, shotgun);
        drawFlashSpikes(g2, rnd, life, shotgun);
        drawFlashSparks(g2, rnd, life, shotgun);
 
        g2.setTransform(old);
        g2.setPaint(oldPaint);
        g2.setStroke(oldStroke);
    }
 
    /** Resplandor redondo + luz tenue que "ilumina" la zona oscura alrededor. */
    private void drawFlashGlow(Graphics2D g2, double life, boolean shotgun) {
        Point2D center = new Point2D.Double(0, 0);
        float[] fractions = {0f, 0.35f, 1f};
 
        // Iluminación ambiente
        double ambient = shotgun ? 190 : 140;
        g2.setPaint(new RadialGradientPaint(center, (float) ambient, fractions, new Color[]{
                new Color(255, 170, 70, (int) (70 * life)),
                new Color(255, 120, 30, (int) (35 * life)),
                new Color(255, 100, 0, 0)}));
        g2.fill(new Ellipse2D.Double(-ambient, -ambient, ambient * 2, ambient * 2));
 
        // Núcleo brillante
        double radius = (shotgun ? 70 : 50) * (0.6 + 0.4 * life);
        g2.setPaint(new RadialGradientPaint(center, (float) radius, fractions, new Color[]{
                new Color(255, 245, 190, (int) (230 * life)),
                new Color(255, 150, 40, (int) (150 * life)),
                new Color(255, 100, 0, 0)}));
        g2.fill(new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2));
    }
 
    /** Picos de fuego en abanico hacia adelante. */
    private void drawFlashSpikes(Graphics2D g2, Random rnd, double life, boolean shotgun) {
        int count = shotgun ? 9 : 6;
        double spread = shotgun ? 0.75 : 0.5;     // apertura del abanico (rad)
        double baseLength = shotgun ? 70 : 48;
 
        Path2D outer = new Path2D.Double();
        Path2D inner = new Path2D.Double();
 
        for (int i = 0; i < count; i++) {
            // El primer pico va derecho (el más largo); el resto se reparte en abanico
            double angle = (i == 0) ? 0 : (rnd.nextDouble() * 2 - 1) * spread;
            double length = baseLength * (i == 0 ? 1.2 : 0.45 + rnd.nextDouble() * 0.6) * (0.5 + 0.5 * life);
            double halfWidth = (i == 0 ? 7 : 4) * (0.6 + 0.4 * life);
 
            addSpike(outer, angle, length, halfWidth);
            addSpike(inner, angle, length * 0.55, halfWidth * 0.5);
        }
 
        g2.setPaint(new Color(255, 160, 40, (int) (230 * life)));
        g2.fill(outer);
        g2.setPaint(new Color(255, 250, 215, (int) (255 * life)));
        g2.fill(inner);
    }
 
    /** Triángulo fino desde la boca del arma hacia el ángulo indicado. */
    private void addSpike(Path2D path, double angle, double length, double halfWidth) {
        double dx = Math.cos(angle);
        double dy = Math.sin(angle);
        double px = -dy * halfWidth; // perpendicular
        double py = dx * halfWidth;
 
        path.moveTo(px, py);
        path.lineTo(dx * length, dy * length);
        path.lineTo(-px, -py);
        path.closePath();
    }
 
    /** Chispas chicas que salen disparadas y se alejan mientras el destello se apaga. */
    private void drawFlashSparks(Graphics2D g2, Random rnd, double life, boolean shotgun) {
        int count = shotgun ? 9 : 5;
        double progress = 1.0 - life; // 0 -> 1
 
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 
        for (int i = 0; i < count; i++) {
            double angle = (rnd.nextDouble() * 2 - 1) * 0.9;
            double maxDist = 30 + rnd.nextDouble() * (shotgun ? 70 : 45);
            double start = 6 + progress * maxDist;
            double end = start + 5 + rnd.nextDouble() * 7;
 
            g2.setPaint(new Color(255, 215, 120, (int) (255 * life)));
            g2.draw(new Line2D.Double(
                    Math.cos(angle) * start, Math.sin(angle) * start,
                    Math.cos(angle) * end, Math.sin(angle) * end));
        }
    }
 
    private static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
    }
}