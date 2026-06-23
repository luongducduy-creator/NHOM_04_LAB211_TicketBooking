package com.nhom04.ticketbooking.model.match;

import com.nhom04.ticketbooking.model.base.BaseEntity;

/**
 * Entity Match - biểu diễn một trận đấu.
 */
public class Match extends BaseEntity {
    private String homeTeam;
    private String awayTeam;
    private String date;        // có thể dùng LocalDate nếu muốn
    private MatchStatus status;

    public Match() {}

    public Match(String id, String homeTeam, String awayTeam, String date, MatchStatus status) {
        super(id);
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
        this.status = status;
    }

    // Getter & Setter
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    @Override
    public String toCsvLine() {
        return String.join(",", getId(), homeTeam, awayTeam, date, status.name());
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",");
        setId(parts[0]);
        this.homeTeam = parts[1];
        this.awayTeam = parts[2];
        this.date = parts[3];
        this.status = MatchStatus.valueOf(parts[4]);
    }
}
