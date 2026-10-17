import controller.MenuController;
import model.MatchDAO;
import persistence.DatabaseConnection;
import persistence.MatchDAOMemory;
import persistence.MatchDAOSQLite;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        MatchDAO matchDAO = createMatchDAO();
        new MenuController(matchDAO);
    }

    private static MatchDAO createMatchDAO() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            return new MatchDAOSQLite(connection);
        } catch (SQLException e) {
            return new MatchDAOMemory();
        }
    }
}