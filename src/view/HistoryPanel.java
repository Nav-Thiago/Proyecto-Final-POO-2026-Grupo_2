package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel {

    private Image backgroundImage;
    private static final String[] COLUMNS = {"#", "Fecha", "Tiempo", "Zombies", "Daño causado", "Daño recibido"};

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final CardLayout centerLayout = new CardLayout();
    private final JPanel center = new JPanel(centerLayout);
    private final MenuButton backButton = new MenuButton("Volver");

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 15, 15));
        setBorder(BorderFactory.createEmptyBorder(40, 120, 40, 120));

        java.net.URL imgUrl = getClass().getResource("/Resources/imagenes/fondo_menu_historial.png");
        if (imgUrl != null) {
            this.backgroundImage = new ImageIcon(imgUrl).getImage();
        }

        JLabel title = new JLabel("HISTORIAL", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(new Color(149, 6, 6));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JScrollPane scroll = new JScrollPane(createTable());
        scroll.getViewport().setBackground(new Color(20, 20, 20));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 0, 0)));

        JLabel emptyLabel = new JLabel("Todavía no hay partidas ganadas", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Arial", Font.BOLD, 22));
        emptyLabel.setForeground(Color.WHITE);

        center.setOpaque(false);
        center.add(scroll, "TABLE");
        center.add(emptyLabel, "EMPTY");

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        south.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        south.add(backButton);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    private JTable createTable() {
        JTable table = new JTable(tableModel);
        table.setBackground(new Color(20, 20, 20));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Monospaced", Font.PLAIN, 18));
        table.setRowHeight(34);
        table.setGridColor(new Color(90, 0, 0));
        table.setRowSelectionAllowed(false);
        table.setFocusable(false);
        table.setFillsViewportHeight(true);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, cellRenderer);

        DefaultTableCellRenderer headRender = new DefaultTableCellRenderer();
        headRender.setHorizontalAlignment(SwingConstants.CENTER);
        headRender.setBackground(new Color(60, 0, 0));
        headRender.setForeground(Color.WHITE);
        table.getTableHeader().setDefaultRenderer(headRender);
        table.getTableHeader().setReorderingAllowed(false);

        return table;
    }

    public void showRows(List<Object[]> rows) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) {
            tableModel.addRow(new Object[] {row[0], row[1], formatTime((long) row[2]), row[3], row[4], row[5]
            });
        }
        centerLayout.show(center, rows.isEmpty() ? "EMPTY" : "TABLE");
    }

    private String formatTime(long ms) {
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public MenuButton getBackButton() {
        return backButton;
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
