package view.hud;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

// Viñeta de color en los bordes de la pantalla: rojo al recibir daño, celeste si lo absorbe el escudo, color del power up al agarrarlo. 
public class ScreenFlash extends HudElement {

    private static class Pulse {
        final Color color;
        double strength;
        final double decay;

        Pulse(Color color, double strength, double decay) {
            this.color = color;
            this.strength = strength;
            this.decay = decay;
        }
    }

    private final List<Pulse> pulses = new ArrayList<>();
    private int lastHealth = -1;
    private int lastShield = -1;
    private double lowHealth; // 0 sano .. 1 casi muerto
    private double time;

    @Override
    public void update(HudData data, double dt) {
        time += dt;
        if (lastHealth >= 0) {
            if (data.health() < lastHealth) {
                pulses.add(new Pulse(new Color(220, 20, 30), 0.95, 1.5));
            } else if (data.shield() < lastShield) {
                pulses.add(new Pulse(PowerUpStyle.SHIELD.color(), 0.6, 2.0));
            }
        }
        lastHealth = data.health();
        lastShield = data.shield();

        for (Pulse p : pulses) p.strength -= p.decay * dt;
        pulses.removeIf(p -> p.strength <= 0);

        double ratio = data.maxHealth() > 0 ? data.health() / (double) data.maxHealth() : 1;
        lowHealth = data.health() > 0 ? clamp01((0.3 - ratio) / 0.3) : 0;
    }

    @Override
    public void onPickup(PowerUpStyle style) {
        pulses.add(new Pulse(style.color(), 0.5, 2.2));
    }

    @Override
    public void draw(Graphics2D g, int screenW, int screenH) {
        for (Pulse p : pulses) {
            vignette(g, screenW, screenH, p.color, p.strength * 0.8);
        }
        if (lowHealth > 0) {
            double beat = 0.55 + 0.45 * Math.sin(time * 5);
            vignette(g, screenW, screenH, new Color(200, 0, 15), lowHealth * beat * 0.55);
        }
    }

    private void vignette(Graphics2D g, int w, int h, Color c, double strength) {
        float radius = (float) Math.hypot(w, h) / 2f;
        g.setPaint(new RadialGradientPaint(
                new Point2D.Float(w / 2f, h / 2f), radius,
                new float[]{0.4f, 1f},
                new Color[]{alpha(c, 0), alpha(c, strength)}));
        g.fillRect(0, 0, w, h);
    }
}
