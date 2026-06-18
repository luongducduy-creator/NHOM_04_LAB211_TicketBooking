package model.ticket;

public class Ticket {
    private String ticketId;
    private String matchId;
    private String seatId;
    private String seatType;
    private double price;
    private String date;
    private TicketStatus status;

    // Constructor
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

    // Getter
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

    // Hiển thị lại thành CSV
    @Override
    public String toString() {
        return ticketId + "," + matchId + "," + seatId + "," + seatType + "," +
                price + "," + date + "," + status;
    }

    // Parse từ một dòng CSV
    public static Ticket fromCsv(String line) {
        if (line == null || line.trim().isEmpty())
            return null;

        String[] parts = line.split(",");
        // bỏ qua header
        if (parts[0].equalsIgnoreCase("ticketId"))
            return null;
        if (parts.length < 7)
            return null;

        try {
            String ticketId = parts[0].trim();
            String matchId = parts[1].trim();
            String seatId = parts[2].trim();
            String seatType = parts[3].trim();
            double price = Double.parseDouble(parts[4].trim());
            String date = parts[5].trim();
            TicketStatus status = TicketStatus.valueOf(parts[6].trim().toUpperCase());

            return new Ticket(ticketId, matchId, seatId, seatType, price, date, status);
        } catch (Exception e) {
            // Nếu parse lỗi thì bỏ qua dòng đó
            return null;
        }
    }
}
