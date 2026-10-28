package view;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    
    private Image backgroundImage;

    private MenuButton playButton;
    private MenuButton optionButton;
    private MenuButton exitButton;

    private java.util.List<Ceniza> particulas = new java.util.ArrayList<>();
    private javax.swing.Timer particleTimer;

    private class Ceniza {
        int x, y, size, speed;
        
        public Ceniza(int width, int height) {
            x = (int) (Math.random() * width);
            y = (int) (Math.random() * height);
            size = (int) (Math.random() * 4) + 2; // Tamaño entre 2 y 5 píxeles
            speed = (int) (Math.random() * 3) + 1; // Velocidad de caída
        }
        
        public void update(int width, int height) {
            y += speed;
            x -= 1; // Viento suave hacia la izquierda
            
            // Si sale de la pantalla, reaparece arriba
            if (y > height) y = 0;
            if (x < 0) x = width;
        }
    }

    public MenuPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(150, 50, 0, 0));

        java.net.URL imgUrl = getClass().getResource("/Resources/imagenes/fondo_menu_principal6.png");
        if (imgUrl != null) {
            this.backgroundImage = new ImageIcon(imgUrl).getImage();
        } else {
            System.err.println("No se encontró el fondo del menú principal");
        }
        
        JLabel titleLabel = new JLabel("NO WAY BACK");
        try {
            // Cargamos el archivo de la fuente
            java.io.InputStream fontStream = getClass().getResourceAsStream("/Resources/fonts/Nosifer-Regular.ttf");
            if (fontStream != null) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream); //Nosifer-Regular.ttf

                // damos el estilo y el tamaño deseado (el 60f es un float que representa los píxeles)
                Font titleFont = customFont.deriveFont(Font.BOLD, 60f);

                // aplicamos
                titleLabel.setForeground(new Color(149,6 ,6));
                titleLabel.setFont(titleFont);
                titleLabel.setAlignmentX(LEFT_ALIGNMENT);
            } else {
                System.err.println("No se encontró la fuente tipográfica");
            }

        } catch (Exception e) {
            System.out.println("Error al cargar la fuente, usando Arial por defecto.");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        }   

        playButton = new MenuButton("Jugar");
        optionButton = new MenuButton("Opciones");
        exitButton = new MenuButton("Salir");

        playButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        exitButton.setAlignmentX(Component.LEFT_ALIGNMENT);


        add(Box.createVerticalGlue());

        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 80)));
        add(playButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(optionButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(exitButton);

        add(Box.createVerticalGlue());

        // Generar 70 partículas iniciales (asumiendo un ancho base de 1920x1080 para esparcirlas)
        for (int i = 0; i < 70; i++) {
            particulas.add(new Ceniza(1920, 1080));
        }

        // Bucle de animación (se actualiza cada 16ms = ~60 FPS)
        particleTimer = new javax.swing.Timer(16, e -> {
            int currentWidth = getWidth() > 0 ? getWidth() : 1920;
            int currentHeight = getHeight() > 0 ? getHeight() : 1080;
            
            for (Ceniza c : particulas) {
                c.update(currentWidth, currentHeight);
            }
            repaint(); // Obliga a redibujar el panel con las nuevas posiciones
        });
        particleTimer.start();

        
    }

    public MenuButton getPlayButton() { return playButton; }
    public MenuButton getOptionButton() { return optionButton; }
    public MenuButton getExitButton() { return exitButton; }


    @Override 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            g.setColor(new Color(10, 12, 18, 140));
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Dibujar las partículas de ceniza/nieve
        g.setColor(new Color(200, 200, 200, 100)); // Gris claro semitransparente
        for (Ceniza c : particulas) {
            g.fillOval(c.x, c.y, c.size, c.size);
        }
    }
}

