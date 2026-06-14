package model.ticket;

public class Ticket {
    private String ticketId;
    private String matchId;
    private String seatId;
    private String type; // VIP / NORMAL
    private int price;
    private String date;
    private TicketStatus status;

    public Ticket(String ticketId, String matchId, String seatId, String type,
            int price, String date, TicketStatus status) {
        this.ticketId = ticketId;
        this.matchId = matchId;
        this.seatId = seatId;
        this.type = type;
        this.price = price;
        this.date = date;
        this.status = status;
    }

    // Parse từ CSV → Object
    public static Ticket fromCsvLine(String line) {
        String[] parts = line.split(",");
        return new Ticket(
                parts[0], parts[1], parts[2], parts[3],
                Integer.parseInt(parts[4]), parts[5],
                TicketStatus.valueOf(parts[6].toUpperCase()));
    }

    // Object → CSV
    public String toCsvLine() {
        return String.join(",", ticketId, matchId, seatId, type,
                String.valueOf(price), date, status.name());
    }

    // Getter/Setter
    public String getTicketId() {
        return ticketId;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getSeatId() {
        return seatId;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
