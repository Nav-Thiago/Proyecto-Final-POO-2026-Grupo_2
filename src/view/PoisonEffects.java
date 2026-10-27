package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Random;


  //Efectos visuales del veneno: el escupitajo del FinalBoss (BossSpit).
 
 
public class PoisonEffects {

    private static final int BURST_TICKS = 16;       // duracion de la salpicadura al escupir
    private static final int TRAIL_SEGMENTS = 9;     // "gotas" que forman la estela
    private static final int DRIPS = 6;              // gotitas sueltas que se despegan de la estela
    private static final double MOUTH_OFFSET = 40;   // la salpicadura sale del borde del boss, no de su centro

    // BOSS SPIT
    public void drawBossSpit(Graphics2D g2, double x, double y, int w, int h, double dirX, double dirY, double speed, int age) {
        Paint oldPaint = g2.getPaint();
        Stroke oldStroke = g2.getStroke();
        AffineTransform oldTransform = g2.getTransform();
        Object oldAntialias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double centerX = x + w / 2.0;
        double centerY = y + h / 2.0;
        double radius = w / 2.0;
        double angle = Math.atan2(dirY, dirX);
        double travelled = age * speed; // cuanto recorrio desde que salio

        drawLaunchBurst(g2, centerX, centerY, dirX, dirY, angle, travelled, age);
        drawTrail(g2, centerX, centerY, dirX, dirY, radius, speed, travelled, age);
        drawGlow(g2, centerX, centerY, radius, age);
        drawBlob(g2, centerX, centerY, angle, radius, age);

        g2.setTransform(oldTransform);
        g2.setPaint(oldPaint);
        g2.setStroke(oldStroke);
        if (oldAntialias != null) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialias);
        }
    }

    // Salpicadura verde en la boca del boss durante los primeros ticks: anillo, resplandor y gotas disparadas. */
    private void drawLaunchBurst(Graphics2D g2, double cx, double cy, double dirX, double dirY, double angle, double travelled, int age) {
        if (age >= BURST_TICKS) return;

        double progress = age / (double) BURST_TICKS; // 0 -> 1
        double life = 1.0 - progress;                 // 1 -> 0
        double eased = 1.0 - life * life;             // sale rapido y frena

        // El origen se deduce de donde esta ahora la bala: no hace falta guardarlo
        double originX = cx - dirX * travelled + dirX * MOUTH_OFFSET;
        double originY = cy - dirY * travelled + dirY * MOUTH_OFFSET;

        // Resplandor
        double glowRadius = 60 * (0.6 + 0.4 * life);
        g2.setPaint(new RadialGradientPaint(new Point2D.Double(originX, originY), (float) glowRadius,
                new float[]{0f, 0.4f, 1f},
                new Color[]{
                        new Color(200, 255, 100, (int) (210 * life)),
                        new Color(70, 205, 45, (int) (110 * life)),
                        new Color(30, 150, 30, 0)}));
        g2.fill(new Ellipse2D.Double(originX - glowRadius, originY - glowRadius, glowRadius * 2, glowRadius * 2));

        // Anillo que se expande
        double ringRadius = 12 + 44 * eased;
        g2.setStroke(new BasicStroke((float) (1.5 + 4 * life), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setPaint(alpha(new Color(150, 240, 70), (int) (200 * life)));
        g2.draw(new Ellipse2D.Double(originX - ringRadius, originY - ringRadius, ringRadius * 2, ringRadius * 2));

        // Gotas disparadas en abanico hacia adelante
        Random rnd = new Random(31);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 9; i++) {
            double a = angle + (rnd.nextDouble() * 2 - 1) * 0.95;
            double maxDist = 24 + rnd.nextDouble() * 52;
            double start = 8 + eased * maxDist;
            double end = start + 4 + rnd.nextDouble() * 8 * life;
            double size = (2 + rnd.nextDouble() * 3.5) * (0.4 + 0.6 * life);

            double sx = originX + Math.cos(a) * start;
            double sy = originY + Math.sin(a) * start;

            g2.setPaint(alpha(new Color(110, 225, 55), (int) (190 * life)));
            g2.draw(new Line2D.Double(sx, sy, originX + Math.cos(a) * end, originY + Math.sin(a) * end));
            g2.setPaint(alpha(new Color(170, 250, 90), (int) (240 * life)));
            g2.fill(new Ellipse2D.Double(sx - size, sy - size, size * 2, size * 2));
        }
    }

    // Estela de baba que se va achicando y ondula, mas gotitas que se despegan y se abren hacia los costados. */
    private void drawTrail(Graphics2D g2, double cx, double cy, double dirX, double dirY, double radius, double speed, double travelled, int age) {
        double perpX = -dirY;
        double perpY = dirX;
        double step = speed * 0.9;

        // La estela crece desde 0 los primeros ticks: parece que "sale" del boss
        double maxLength = travelled + 6;

        for (int i = TRAIL_SEGMENTS; i >= 1; i--) {
            double dist = i * step;
            if (dist > maxLength) continue;

            double f = i / (double) TRAIL_SEGMENTS;           // 0 cerca de la bola -> 1 en la cola
            double wobble = Math.sin(age * 0.45 + i * 0.9) * 5 * f;
            double px = cx - dirX * dist + perpX * wobble;
            double py = cy - dirY * dist + perpY * wobble;

            double r = radius * (0.78 - 0.6 * f);
            int a = (int) (175 * Math.pow(1 - f, 1.3));

            g2.setPaint(new Color(35, 160, 40, a));
            g2.fill(new Ellipse2D.Double(px - r, py - r, r * 2, r * 2));
            double inner = r * 0.5;
            g2.setPaint(new Color(140, 240, 75, (int) (a * 0.7)));
            g2.fill(new Ellipse2D.Double(px - inner, py - inner, inner * 2, inner * 2));
        }

        // Gotitas sueltas: cada una nace pegada a la bola, se despega y se abre hacia un costado
        for (int j = 0; j < DRIPS; j++) {
            double lifeJ = ((age + j * 5) % 24) / 24.0;       // 0 -> 1 y vuelve a empezar
            double dist = 12 + lifeJ * 80;
            if (dist > maxLength) continue;

            double side = (j % 2 == 0 ? 1 : -1) * (5 + lifeJ * 15);
            double size = 1 + 3 * (1 - lifeJ);
            double dx = cx - dirX * dist + perpX * side;
            double dy = cy - dirY * dist + perpY * side;

            g2.setPaint(new Color(120, 230, 60, (int) (210 * (1 - lifeJ))));
            g2.fill(new Ellipse2D.Double(dx - size, dy - size, size * 2, size * 2));
        }
    }

    // Resplandor toxico que pulsa alrededor de la bola. 
    private void drawGlow(Graphics2D g2, double cx, double cy, double radius, int age) {
        double pulse = 0.5 + 0.5 * Math.sin(age * 0.4);
        double glowRadius = radius * (2.3 + 0.4 * pulse);

        g2.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), (float) glowRadius,
                new float[]{0f, 0.4f, 1f},
                new Color[]{
                        new Color(130, 255, 70, (int) (110 + 40 * pulse)),
                        new Color(60, 200, 40, 60),
                        new Color(30, 150, 30, 0)}));
        g2.fill(new Ellipse2D.Double(cx - glowRadius, cy - glowRadius, glowRadius * 2, glowRadius * 2));
    }

    // La bola en si: una gota alargada que se deforma, con burbujas adentro y un brillo. 
    private void drawBlob(Graphics2D g2, double cx, double cy, double angle, double radius, int age) {
        // A partir de aca el eje X apunta hacia donde viaja el escupitajo y (0,0) es el centro de la bola
        g2.translate(cx, cy);
        g2.rotate(angle);

        // Contorno: 12 puntos que "respiran" y cola mas larga atras (forma de gota)
        int n = 12;
        double[] px = new double[n];
        double[] py = new double[n];
        for (int k = 0; k < n; k++) {
            double a = Math.PI * 2 * k / n;
            double wobble = 1 + 0.10 * Math.sin(age * 0.55 + k * 1.7) + 0.06 * Math.sin(age * 0.9 + k * 3.1);
            double stretch = Math.cos(a) < 0 ? 1.5 : 1.05;
            px[k] = Math.cos(a) * radius * wobble * stretch;
            py[k] = Math.sin(a) * radius * wobble * 0.95;
        }
        Path2D blob = smoothClosedPath(px, py);

        // Cuerpo con degradado: centro claro, borde oscuro
        g2.setPaint(new RadialGradientPaint(new Point2D.Double(0, 0), (float) (radius * 1.5),
                new Point2D.Double(radius * 0.25, -radius * 0.3),
                new float[]{0f, 0.45f, 1f},
                new Color[]{new Color(205, 255, 115), new Color(80, 215, 50), new Color(20, 110, 35)},
                CycleMethod.NO_CYCLE));
        g2.fill(blob);

        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setPaint(new Color(15, 90, 25, 200));
        g2.draw(blob);

        // Burbujas que giran adentro
        for (int k = 0; k < 4; k++) {
            double a = age * 0.12 * (k % 2 == 0 ? 1 : -1) + k * 1.6;
            double dist = radius * 0.45 * (0.5 + 0.5 * Math.sin(age * 0.2 + k));
            double bx = Math.cos(a) * dist;
            double by = Math.sin(a) * dist;
            double br = 2 + (k % 3);

            Ellipse2D bubble = new Ellipse2D.Double(bx - br, by - br, br * 2, br * 2);
            g2.setPaint(new Color(215, 255, 165, 120));
            g2.fill(bubble);
            g2.setStroke(new BasicStroke(1f));
            g2.setPaint(new Color(240, 255, 225, 190));
            g2.draw(bubble);
        }

        // Brillo
        g2.setPaint(new Color(235, 255, 225, 150));
        g2.fill(new Ellipse2D.Double(radius * 0.1, -radius * 0.62, radius * 0.5, radius * 0.25));
    }

    // Curva cerrada y suave que pasa cerca de los puntos dados (usa los puntos medios como anclas). 
    private Path2D smoothClosedPath(double[] px, double[] py) {
        int n = px.length;
        Path2D path = new Path2D.Double();
        path.moveTo((px[n - 1] + px[0]) / 2, (py[n - 1] + py[0]) / 2);
        for (int k = 0; k < n; k++) {
            int next = (k + 1) % n;
            path.quadTo(px[k], py[k], (px[k] + px[next]) / 2, (py[k] + py[next]) / 2);
        }
        path.closePath();
        return path;
    }

    private static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
    }
}
