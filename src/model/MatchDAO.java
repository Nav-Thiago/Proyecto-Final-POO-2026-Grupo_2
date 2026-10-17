package model;

import java.util.List;

public interface MatchDAO {
    boolean save (MatchRecord match);
    List<MatchRecord> bestSurvivalTimes(int limit);
}
