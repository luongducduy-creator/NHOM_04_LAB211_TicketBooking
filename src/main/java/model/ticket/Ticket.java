package model.ticket;

import java.util.Objects;

public class Ticket {

    private String ticketId;
    private String matchId;
    private String seatId;
    private String seatType;
    private double price;
    private String date;
    private TicketStatus status;

    public Ticket(String ticketId, String matchId, String seatId, String seatType,
            double price, String date, TicketStatus status) {
        this.ticketId = ticketId;
        this.matchId = matchId;
        this.seatId = seatId;
        this.seatType = seatType;
        this.price = price;
        this.date = date;
        this.status = status;
    }

    // ===== GETTERS =====
    public String getTicketId() {
        return ticketId;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getSeatId() {
        return seatId;
    }

    public String getSeatType() {
        return seatType;
    }

    public double getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public TicketStatus getStatus() {
        return status;
    }

    // Cho phép đổi trạng thái
    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return ticketId + "," + matchId + "," + seatId + "," +
                seatType + "," + price + "," + date + "," + status;
    }

    // ===== PARSER TỪ CSV =====
    public static Ticket fromCsv(String line) {
        if (line == null || line.isBlank())
            return null;

        String[] parts = line.split(",");
        if (parts.length < 7)
            return null;

        if (parts[0].trim().equalsIgnoreCase("ticketId"))
            return null;

        try {
            String ticketId = parts[0].trim();
            String matchId = parts[1].trim();
            String seatId = parts[2].trim();
            String seatType = parts[3].trim();
            double price = Double.parseDouble(parts[4].trim());
            String date = parts[5].trim();
            TicketStatus status = parseStatus(parts[6].trim());

            if (status == null)
                return null;

            return new Ticket(ticketId, matchId, seatId, seatType, price, date, status);

        } catch (Exception e) {
            return null;
        }
    }

    private static TicketStatus parseStatus(String raw) {
        if (raw == null)
            return null;
        switch (raw.trim().toUpperCase()) {
            case "SOLD":
                return TicketStatus.SOLD;
            case "AVAILABLE":
                return TicketStatus.AVAILABLE;
            case "CANCELLED":
                return TicketStatus.CANCELLED;
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Ticket))
            return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(ticketId, ticket.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }
}
