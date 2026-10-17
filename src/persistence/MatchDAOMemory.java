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
    public List<MatchRecord> bestSurvivalTimes(int limit) {
        if (limit <= 0) {
            return new ArrayList<>();
        }
        List<MatchRecord> sorted = new ArrayList<>(matches);
        sorted.sort(Comparator.comparingLong(MatchRecord::getSurvivalTimeMs).reversed());
        return new ArrayList<>(sorted.subList(0, Math.min(limit, sorted.size())));
    }
}
