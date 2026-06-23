package model;

import java.time.LocalDate;

public class Match {
    private String id;
    private String homeTeam;
    private String awayTeam;
    private LocalDate date;
    private String stadiumId;
    private MatchStatus status;

    // Constructor rỗng
    public Match() {}

    // Constructor đầy đủ
    public Match(String id, String homeTeam, String awayTeam, LocalDate date, String stadiumId) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
        this.stadiumId = stadiumId;
        this.status = MatchStatus.SCHEDULED;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getStadiumId() { return stadiumId; }
    public void setStadiumId(String stadiumId) { this.stadiumId = stadiumId; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    // Alias
    public String getMatchId() { return id; }

    // CSV hỗ trợ
    public String toCsvLine() {
        return id + "," + homeTeam + "," + awayTeam + "," + date + "," + stadiumId + "," + status;
    }

    public void fromCsvLine(String line) {
        String[] parts = line.split(",");
        this.id = parts[0];
        this.homeTeam = parts[1];
        this.awayTeam = parts[2];
        this.date = LocalDate.parse(parts[3]);
        this.stadiumId = parts[4];
        this.status = MatchStatus.valueOf(parts[5]);
    }
}
