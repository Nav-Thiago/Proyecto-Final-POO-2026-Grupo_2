package view;

import javax.swing.*;
import java.awt.*;

import controller.AudioManager;
import controller.MenuController;

public class OptionsPanel extends JPanel {

    private Image backgroundImage;
    private MenuController controller;

    public OptionsPanel(MenuController controller) {
        this.controller = controller;

        setLayout(new GridBagLayout());
        setBackground(new Color(15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);

        java.net.URL imgUrl = getClass().getResource("/Resources/imagenes/fondo_menu_opciones2.png");
        if (imgUrl != null) {
            this.backgroundImage = new ImageIcon(imgUrl).getImage();
        }

        JLabel titleLabel = new JLabel("OPCIONES");
        try {
            // Cargamos el archivo de la fuente
            java.io.InputStream fontStream = getClass().getResourceAsStream("/Resources/fonts/Nosifer-Regular.ttf");
            if (fontStream != null) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);

                // damos el estilo y el tamaño deseado (el 60f es un float que representa los píxeles)
                Font titleFont = customFont.deriveFont(Font.BOLD, 60f);

                // aplicamos
                titleLabel.setForeground(new Color(149, 6, 6));
                titleLabel.setFont(titleFont);
                titleLabel.setAlignmentX(CENTER_ALIGNMENT);
            }

        } catch (Exception e) {
            System.out.println("Error al cargar la fuente, usando Arial por defecto.");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        }  
        add(titleLabel, gbc);

        gbc.gridy = 1;
            gbc.insets = new Insets(10, 20, 5, 20);
            JLabel volumeLabel = new JLabel("Volumen de música", SwingConstants.CENTER);
            volumeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            volumeLabel.setForeground(Color.WHITE);
            add(volumeLabel, gbc);
    
            gbc.gridy = 2;
            gbc.insets = new Insets(5, 20, 10, 20);
            JSlider volumeSlider = new JSlider(0, 100, 50);
            volumeSlider.setOpaque(false);
            volumeSlider.setForeground(Color.WHITE);
            volumeSlider.setMajorTickSpacing(25);
            volumeSlider.setPaintTicks(true);
            volumeSlider.setPaintLabels(true);
    
            volumeSlider.addChangeListener(e -> {
                int volume = volumeSlider.getValue();
                AudioManager.setVolume(volume);
            });
            add(volumeSlider, gbc);
    
            gbc.gridy = 3;
            gbc.insets = new Insets(10, 20, 5, 20);
            JLabel sfxLabel = new JLabel("Volumen de efectos", SwingConstants.CENTER);
            sfxLabel.setFont(new Font("Arial", Font.BOLD, 18));
            sfxLabel.setForeground(Color.WHITE);
            add(sfxLabel, gbc);
    
            gbc.gridy = 4;
            gbc.insets = new Insets(5, 20, 10, 20);
            JSlider sfxSlider = new JSlider(0, 100, AudioManager.getSfxVolume());
            sfxSlider.setOpaque(false);
            sfxSlider.setForeground(Color.WHITE);
            sfxSlider.setMajorTickSpacing(25);
            sfxSlider.setPaintTicks(true);
            sfxSlider.setPaintLabels(true);

            sfxSlider.addChangeListener(e -> {
                int volume = sfxSlider.getValue();
                AudioManager.setSfxVolume(volume);
            });
            add(sfxSlider, gbc);

            gbc.gridy = 5;
            gbc.insets = new Insets(10, 20, 5, 20);
            JLabel resLabel = new JLabel("Resolución", SwingConstants.CENTER);
            resLabel.setFont(new Font("Arial", Font.BOLD, 18));
            resLabel.setForeground(Color.WHITE);
            add(resLabel, gbc);

            gbc.gridy = 6;
            gbc.insets = new Insets(5, 20, 15, 20);
            String[] resolutions = {"800 x 600", "1280 x 720", "1920 x 1080"};
            JComboBox<String> resBox = new JComboBox<>(resolutions);
            resBox.setSelectedIndex(1);
            resBox.setFont(new Font("Arial", Font.BOLD, 18));

            resBox.addActionListener(e -> {
                String seleccion = (String) resBox.getSelectedItem();
                if (seleccion != null) {
                    switch (seleccion) {
    			case "800 x 600" -> controller.changeResolution(800, 600);
    			case "1280 x 720" -> controller.changeResolution(1280, 720);
    			case "1920 x 1080" -> controller.changeResolution(1920, 1080);
                    }
                }
            });
            add(resBox, gbc);

            gbc.gridy = 7;
            gbc.insets = new Insets(20, 20, 20, 20);
            MenuButton backButton = new MenuButton("Volver");
            backButton.addActionListener(e -> this.controller.backToMenu());
            add(backButton, gbc);
        }

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
    }
}
