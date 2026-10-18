package persistence;

import model.MatchDAO;
import model.MatchRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MatchDAOMemory implements MatchDAO {

    private final List<MatchRecord> matches = new ArrayList<>();

    @Override
    public boolean save(MatchRecord match) {
        if (match == null) {
            return false;
        }
        return matches.add(match);
    }

    @Override
    public List<MatchRecord> fastestVictories(int limit) {
        if (limit <= 0) {
            return new ArrayList<>();
        }

        List<MatchRecord> victories = new ArrayList<>();

        for (MatchRecord match : matches) {
            if (match.isVictory()) {
                victories.add(match);
            }
        }

        victories.sort(Comparator.comparingLong(MatchRecord::getSurvivalTimeMs));
        return new ArrayList<>(victories.subList(0, Math.min(limit, victories.size())));
    }
}
