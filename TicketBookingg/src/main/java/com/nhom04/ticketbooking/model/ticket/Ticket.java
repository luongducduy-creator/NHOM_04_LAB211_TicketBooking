package com.nhom04.ticketbooking.model.ticket;

import com.nhom04.ticketbooking.model.base.BaseEntity;

public class Ticket extends BaseEntity {
    private String matchId;
    private String seatId;
    private String fanId;
    private double price;

    // ✅ Constructor mặc định (no-args)
    public Ticket() {}

    // Constructor đầy đủ
    public Ticket(String id, String matchId, String seatId, String fanId, double price) {
        super(id); // id nằm trong BaseEntity
        this.matchId = matchId;
        this.seatId = seatId;
        this.fanId = fanId;
        this.price = price;
    }

    // Getter & Setter
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public String getFanId() { return fanId; }
    public void setFanId(String fanId) { this.fanId = fanId; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // ✅ Override BaseEntity methods
    @Override
    public String toCsvLine() {
        return String.join(",",
                getId(),
                matchId,
                seatId,
                fanId,
                String.valueOf(price));
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            setId(parts[0]);
            this.matchId = parts[1];
            this.seatId = parts[2];
            this.fanId = parts[3];
            this.price = Double.parseDouble(parts[4]);
        }
    }
}
