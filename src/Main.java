import controller.MenuController;
import model.MatchDAO;
import persistence.DatabaseConnection;
import persistence.MatchDAOSQLite;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            MatchDAO matchDAO = new MatchDAOSQLite(connection);
            SwingUtilities.invokeLater(() ->new MenuController(matchDAO));
        } catch (SQLException e) {
            System.err.println("ERROR: No se pudo conectar a la base de datos");
            e.printStackTrace();
            System.exit(1);
        }
    }
}