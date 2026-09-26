package view;

import java.awt.image.BufferedImage;

public class Animation {
    private final BufferedImage[] frames;
    private int currentFrame;
    private long lastTime;
    private final int delay; // Milisegundos entre cada fotograma

    public Animation(int delay, boolean isAlive, BufferedImage... frames) {
        this.delay = delay;
        this.frames = frames;
        this.lastTime = System.currentTimeMillis();
    }

    public void update() {
        long timer = System.currentTimeMillis() - this.lastTime;
        if (timer > this.delay) {
            this.currentFrame ++;
            if (this.currentFrame >= this.frames.length) {
                this.currentFrame = 0; // Vuelve al inicio para hacer un bucle
            }
            lastTime = System.currentTimeMillis();
        }
    }

    public BufferedImage getCurrentFrame() {
        return this.frames[this.currentFrame];
    }
}
