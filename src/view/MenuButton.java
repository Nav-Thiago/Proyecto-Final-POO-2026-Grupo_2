package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class MenuButton extends JButton{

    private boolean hover;
    private static final Color NORMAL_BG_COLOR = new Color(15, 15, 15, 200);
    private static final Color HOVER_BG_COLOR = new Color(40, 10, 10, 230);
    private static final Color BORDER_COLOR = new Color(200, 0, 0);
    private static final Color NORMAL_TEXT_COLOR = new Color(200, 200, 200);
    private static final Color HOVER_TEXT_COLOR = Color.WHITE;

    public MenuButton(String text) {
        super(text);
        this.hover = false;

        setFont(new Font("Arial", Font.BOLD, 22));
        setForeground(NORMAL_TEXT_COLOR);

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        setPreferredSize(new Dimension(250, 50));
        setMaximumSize(new Dimension(250, 50));

        setAlignmentX(Component.CENTER_ALIGNMENT);

        addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseEntered(MouseEvent e) {
                hover = true;
                setForeground(HOVER_TEXT_COLOR);
                repaint();
            }

            @Override 
            public void mouseExited(MouseEvent e) {
                hover = false;
                setForeground(NORMAL_TEXT_COLOR);
                repaint();
            }
        });
    }

    @Override 
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        // Fondo del botón
        g2.setColor(hover ? HOVER_BG_COLOR : NORMAL_BG_COLOR);
        g2.fillRect(2, 2, width - 4, height -4);
        // Borde exterior en rojo
        g2.setColor(BORDER_COLOR);
        // Si el mouse está encima, el borde se hace más grueso
        g2.setStroke(new BasicStroke(hover ? 3.0f : 1.0f));
        g2.drawRect(2, 2, width -4, height - 4);

        // Marco rojo tenue extra al hacer hover
        if (hover) {
            g2.setColor(new Color(255, 0, 0, 100));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRect(6, 6, width - 12, height - 12);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
