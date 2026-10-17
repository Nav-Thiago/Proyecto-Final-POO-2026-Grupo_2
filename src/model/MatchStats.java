package model;

public class MatchStats {

    private int zombiesKilled;
    private int damageDealt;
    private long playedTimes;

    public void addKill() {
        zombiesKilled ++;
    }

    public void addDamageDealt(int amount) {
        if (amount > 0) {
            damageDealt += amount;
        }
    }

    public void addPlayedTime(long ms) {
        if (ms > 0) {
            playedTimes += ms;
        }
    }

    public int getZombiesKilled() {
        return zombiesKilled;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public long getPlayedTimes() {
        return playedTimes;
    }
}
