package model.match;

import java.util.Objects;

public class Match {

    private String matchId;
    private String homeTeam;
    private String awayTeam;
    private String date;       // format: yyyy-MM-dd
    private String stadiumId;

    public Match(String matchId, String homeTeam, String awayTeam, String date, String stadiumId) {
        this.matchId   = matchId;
        this.homeTeam  = homeTeam;
        this.awayTeam  = awayTeam;
        this.date      = date;
        this.stadiumId = stadiumId;
    }

    // ===== GETTERS =====
    public String getMatchId()   { return matchId; }
    public String getHomeTeam()  { return homeTeam; }
    public String getAwayTeam()  { return awayTeam; }
    public String getDate()      { return date; }
    public String getStadiumId() { return stadiumId; }

    // ===== SETTERS =====
    public void setHomeTeam(String homeTeam)   { this.homeTeam = homeTeam; }
    public void setAwayTeam(String awayTeam)   { this.awayTeam = awayTeam; }
    public void setDate(String date)           { this.date = date; }
    public void setStadiumId(String stadiumId) { this.stadiumId = stadiumId; }

    /** CSV format: matchId,homeTeam,awayTeam,date,stadiumId */
    public String toCsvLine() {
        return String.join(",", matchId, homeTeam, awayTeam, date, stadiumId);
    }

    public static Match fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split(",");
        if (p.length < 5) return null;
        // Skip header
        if (p[0].trim().equalsIgnoreCase("matchId")) return null;
        return new Match(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s vs %s  |  Date: %s  |  Stadium: %s",
                matchId, homeTeam, awayTeam, date, stadiumId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match m = (Match) o;
        return Objects.equals(matchId, m.matchId);
    }

    @Override
    public int hashCode() { return Objects.hash(matchId); }
}
