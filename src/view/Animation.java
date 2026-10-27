package view;

import java.awt.image.BufferedImage;

public class Animation {
    private final BufferedImage[] frames;
    private int currentFrame;
    private long lastTime;
    private final int delay; // Milisegundos entre cada fotograma

    private double offsetX = 0;
    private double offsetY = 0;
    private double scale = 1.0;

    public Animation(int delay, BufferedImage... frames) {
        this.delay = delay;
        this.frames = frames;
        this.lastTime = System.currentTimeMillis();
    }

    public void update() {
        long timer = System.currentTimeMillis() - this.lastTime;
        if (timer > this.delay) {
            this.currentFrame++;
            if (this.currentFrame >= this.frames.length) {
                this.currentFrame = 0; // Vuelve al inicio para hacer un bucle
            }
            lastTime = System.currentTimeMillis();
        }
    }

    public BufferedImage getCurrentFrame() {
        return this.frames[this.currentFrame];
    }

    public BufferedImage getFrameAt(double progress) {
        int index = (int) (Math.max(0.0, Math.min(1.0, progress)) * this.frames.length);
        return this.frames[Math.min(index, this.frames.length - 1)];
    }

    public Animation withOffset(double offsetX, double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        return this;
    }

    /** Factor de escala extra para igualar el tamaño del cuerpo con el resto de las animaciones. */
    public Animation withScale(double scale) {
        this.scale = scale;
        return this;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getScale() {
        return scale;
    }
}