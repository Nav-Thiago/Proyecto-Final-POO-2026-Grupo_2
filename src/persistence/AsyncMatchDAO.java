package persistence;

import model.MatchDAO;
import model.MatchRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AsyncMatchDAO implements MatchDAO {

    private final MatchDAO realDAO;
    private final ExecutorService worker;

    public AsyncMatchDAO(MatchDAO realDAO) {
        if (realDAO == null) {
            throw new IllegalArgumentException("El DAO es obligatorio");
        }
        this.realDAO = realDAO;
        this.worker = Executors.newSingleThreadExecutor(task -> {
            Thread thread = new Thread(task, "match-dao-worker");
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public boolean save(MatchRecord match) {
        worker.submit(() -> realDAO.save(match));
        return true;
    }

    @Override
    public List<MatchRecord> fastestVictories(int limit) {
        Future<List<MatchRecord>> ticket = worker.submit(() -> realDAO.fastestVictories(limit));
        try {
            return ticket.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } catch (ExecutionException e) {
            return new ArrayList<>();
        }
    }
}
