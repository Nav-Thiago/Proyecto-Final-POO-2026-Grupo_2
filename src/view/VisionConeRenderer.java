package view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

// renderizador del cono de visión y la oscuridad ambiental
// utiliza una máscara de luz "precomputada" con degradado suave (smoothstep)
// y composición alfa (AlphaComposite.DstOut) para evitar bordes cortantes

// se transfiere el método original a una clase individual para una mejor organización y 
// cumplimiento de SRP (Single Responsibility Principle)

public class VisionConeRenderer {

    // --- parámetros de iluminación (configurables) ---
    private final double lightRadius = 700.0;           // alcance máximo del haz de luz (px)
    private final double fovAngle = 85.0;               // angulo de apertura total del cono (grados)
    private final double angularFadeDegrees = 20.0;     // amplitud del degradado en los laterales (grados)
    private final double radialFadeDistance = 140.0;    // longitud del degradado al final del haz (px)
    private final double personalRadius = 90.0;         // radio de visión 360° alrededor del jugador (px)
    private final double personalFadeDistance = 30.0;   // difuminado del borde del círculo personal (px)
    private final int darknessAlpha = 220;              // opacidad de la oscuridad exterior (0 a 255)

    // máscara de luz estática generada una sola vez
    private BufferedImage lightMask;
    private int maskCenter;

    // buffer temporal de pantalla para no reasignar memoria en cada frame
    // anterior método utilizaba Area, Shape y Graphics2D (y calculaba el area en cada frame)
    private BufferedImage darknessBuffer;

    public VisionConeRenderer() {
        initLightMask();
    }

    // genera la textura de luz una sola vez al inicializar
    // calcula la intensidad de cada píxel combinando el cono y el aura personal

    private void initLightMask() { // "init" -> initialize = inicializar
        int r = (int) Math.ceil(lightRadius);
        int d = 2 * r + 4;
        this.maskCenter = r + 2;

        lightMask = new BufferedImage(d, d, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) lightMask.getRaster().getDataBuffer()).getData();

        // Conversión de ángulos a radianes
        double halfFovRad = Math.toRadians(fovAngle / 2.0);
        double fadeAngleRad = Math.toRadians(angularFadeDegrees);
        double coreAngleRad = Math.max(0.0, halfFovRad - fadeAngleRad); // zona central con 100% de luz

        // radios internos donde la luz todavia no empieza a desvanecerse
        double coreRadius = lightRadius - radialFadeDistance;
        double corePersonal = Math.max(0.0, personalRadius - personalFadeDistance);

        for (int y = 0; y < d; y++) {
            int dy = y - maskCenter;
            int dySq = dy * dy;

            for (int x = 0; x < d; x++) {
                int dx = x - maskCenter;
                int distSq = dx * dx + dySq;

                // fuera del alcance máximo: queda completamente transparente (sin luz)
                if (distSq > lightRadius * lightRadius) {
                    continue;
                }

                double dist = Math.sqrt(distSq);

                // aura personal en 360° (visión inmediata alrededor del jugador)
                double circleAlpha = 0.0;
                if (dist <= corePersonal) {
                    circleAlpha = 1.0; // luz total cerca del personaje
                } else if (dist <= personalRadius) {
                    double t = (dist - corePersonal) / personalFadeDistance;
                    circleAlpha = 1.0 - smoothstep(t); // degradado suave hacia afuera
                }

                // cono de visión frontal
                double angle = Math.atan2(dy, dx);
                double absAngle = Math.abs(angle);

                double angularFactor = 0.0;
                if (absAngle <= coreAngleRad) {
                    angularFactor = 1.0; // centro del haz: visibilidad máxima
                } else if (absAngle <= halfFovRad) {
                    double t = (absAngle - coreAngleRad) / fadeAngleRad;
                    angularFactor = 1.0 - smoothstep(t); // difuminado lateral del haz
                }

                // pérdida de intensidad con la distancia (caída frontal)
                double distFactor = 0.0;
                if (angularFactor > 0.0) {
                    if (dist <= coreRadius) {
                        distFactor = 1.0;
                    } else {
                        double t = (dist - coreRadius) / radialFadeDistance;
                        distFactor = 1.0 - smoothstep(t); // difuminado al final del alcance
                    }
                }

                // intensidad resultante del cono
                double coneAlpha = angularFactor * distFactor;

                // combinación suave de cono + círculo personal (unión sin costuras)
                double totalAlpha = 1.0 - (1.0 - coneAlpha) * (1.0 - circleAlpha);

                // guardar valor de transparencia en el canal alpha del píxel
                int a = (int) Math.round(totalAlpha * 255.0);
                if (a > 0) {
                    pixels[y * d + x] = (a << 24) | 0x00FFFFFF;
                }
            }
        }
    }

    // curva de interpolación cúbica (no lineal)
    // suaviza la transición eliminando cortes bruscos en t = 0 y t = 1.
    private double smoothstep(double t) {
        t = Math.max(0.0, Math.min(1.0, t));
        return t * t * (3.0 - 2.0 * t);
    }

    // dibuja la capa de oscuridad sobre la escena aplicando la máscara de luz
    // se ejecuta en cada fotograma dentro del bucle de renderizado

    public void renderDarkness(Graphics2D g2, double playerX, double playerY, int playerW, int playerH,
                               double playerAngle, int screenW, int screenH, int camX, int camY) {
        if (screenW <= 0 || screenH <= 0) return;

        // reutilizar o ajustar buffer según la resolución actual de la ventana
        if (darknessBuffer == null || darknessBuffer.getWidth() != screenW || darknessBuffer.getHeight() != screenH) {
            darknessBuffer = new BufferedImage(screenW, screenH, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D gDark = darknessBuffer.createGraphics();

        // paso a: rellenar la pantalla con la oscuridad ambiental
        gDark.setComposite(AlphaComposite.Src);
        gDark.setColor(new Color(0, 0, 0, darknessAlpha));
        gDark.fillRect(0, 0, screenW, screenH);

        // paso b: activar modo dstout para que la máscara "reste" opacidad a la oscuridad
        gDark.setComposite(AlphaComposite.DstOut);

        // posición del centro del jugador en coordenadas de pantalla
        double centerX = playerX + (playerW / 2.0);
        double centerY = playerY + (playerH / 2.0);
        double screenPlayerX = centerX - camX;
        double screenPlayerY = centerY - camY;

        // paso c: rotar y posicionar la máscara de luz según el ángulo hacia el cursor
        AffineTransform at = AffineTransform.getTranslateInstance(screenPlayerX, screenPlayerY);
        at.rotate(playerAngle);
        at.translate(-maskCenter, -maskCenter);

        gDark.drawImage(lightMask, at, null);
        gDark.dispose();

        // paso d: estampar la capa de oscuridad resultante sobre la pantalla del juego
        g2.drawImage(darknessBuffer, camX, camY, null);
    }
}
