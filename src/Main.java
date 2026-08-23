import view.MainFrame;

import javax.swing.*;

public class Main {

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // Crea un bloque de código para ponerlo en la fila del EDT
            MainFrame ventana = new MainFrame();
            ventana.setVisible(true); // Le indica a la ventana que debe dibujarse en pantalla
        });
    }
}
