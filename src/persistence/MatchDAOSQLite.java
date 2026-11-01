package persistence;

import model.MatchDAO;
import model.MatchRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MatchDAOSQLite implements MatchDAO {

    private final Connection connection;

    public MatchDAOSQLite(Connection connection) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("La conexión es obligatoria");
        }
        this.connection = connection;

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS matches ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "match_date TEXT NOT NULL, "
                    + "survival_time_ms INTEGER NOT NULL, "
                    + "zombies_killed INTEGER NOT NULL, "
                    + "damage_dealt INTEGER NOT NULL, "
                    + "damage_taken INTEGER NOT NULL, "
                    + "victory INTEGER NOT NULL)");
        }
    }

    @Override
    public boolean save(MatchRecord match) {
        if (match == null) {
            return false;
        }

        String sql = "INSERT INTO matches (match_date, survival_time_ms, zombies_killed, damage_dealt, damage_taken, victory) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, match.getDate().toString());
            statement.setLong(2, match.getSurvivalTimeMs());
            statement.setInt(3, match.getZombiesKilled());
            statement.setInt(4, match.getDamageDealt());
            statement.setInt(5, match.getDamageTaken());
            statement.setInt(6, match.isVictory() ? 1 : 0);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo guardar la partida: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<MatchRecord> fastestVictories(int limit) {
        List<MatchRecord> result = new ArrayList<>();

        if (limit <= 0) {
            return result;
        }

        String sql = "SELECT match_date, survival_time_ms, zombies_killed, damage_dealt,"
                + "damage_taken, victory FROM matches WHERE victory = 1 "
                + " ORDER BY survival_time_ms ASC LIMIT ?";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new MatchRecord(
                            LocalDate.parse(resultSet.getString("match_date")),
                            resultSet.getLong("survival_time_ms"),
                            resultSet.getInt("zombies_killed"),
                            resultSet.getInt("damage_dealt"),
                            resultSet.getInt("damage_taken"),
                            resultSet.getInt("victory") == 1));
                }
            }

        } catch (SQLException e) {
            System.err.println("No se pudo leer el historial: " + e.getMessage());
            return result;
        }
        return result;
    }
}
