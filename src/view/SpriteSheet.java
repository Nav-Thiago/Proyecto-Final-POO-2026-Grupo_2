package view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet {
    private BufferedImage sheet;

    public SpriteSheet(String path) {
        try {
            this.sheet = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            System.err.println("Error cargando el sprite sheet: " + e.getMessage());
        }
    }

    // col y row son la posición en la cuadrícula
    public BufferedImage getFrame(int col, int row, int width, int height) {
        return this.sheet.getSubimage(col * width, row * height, width, height);
    }
}
