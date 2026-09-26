package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;


public class SplashUniversity extends SplashPanel {

    private Image logo;

    public SplashUniversity() {
        super(Color.BLACK);
        initializeComponents();
    }

    @Override 
    protected void initializeComponents() {
        java.net.URL logoUrl = getClass().getResource("/Resources/imagenes/universidad.png");
        if (logoUrl != null) {
            logo = new ImageIcon(logoUrl).getImage();
        } else {
            System.err.println("No se encontró el logo de la universidad");
        }
    }

    @Override 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (logo != null) {
            int imgWidth = 1920;
            int imgHeight = 1080;

            int x = (getWidth() - imgWidth) / 2;
            int y = (getHeight() - imgHeight) / 2;
            
            g.drawImage(logo, x, y, imgWidth, imgHeight, this);
        }
    }
}
