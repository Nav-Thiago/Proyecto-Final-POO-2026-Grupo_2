package model;

import java.time.LocalDate;

public class MatchRecord {

    private final LocalDate date;
    private final long survivalTimeMs;
    private final int zombiesKilled;
    private final int damageDealt;
    private final int damageTaken;
    private final boolean victory;

    public MatchRecord (LocalDate date, long survivalTimeMs, int zombiesKilled, int damageDealt, int damageTaken, boolean victory) {
        if (date == null || survivalTimeMs < 0 || zombiesKilled < 0 || damageDealt < 0 || damageTaken < 0) {
            throw new IllegalArgumentException("Datos de la partida inválidos");
        }

        this.date = date;
        this.survivalTimeMs = survivalTimeMs;
        this.zombiesKilled = zombiesKilled;
        this.damageDealt = damageDealt;
        this.damageTaken = damageTaken;
        this.victory = victory;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getSurvivalTimeMs() {
        return survivalTimeMs;
    }

    public int getZombiesKilled() {
        return zombiesKilled;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public int getDamageTaken() {
        return damageTaken;
    }

    public boolean isVictory() {
        return victory;
    }
}
