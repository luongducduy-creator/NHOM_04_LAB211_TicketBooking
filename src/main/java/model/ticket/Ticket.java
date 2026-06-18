package model.ticket;

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

    @Override
    public String toString() {
        return ticketId + "," + matchId + "," + seatId + "," + seatType + "," +
                price + "," + date + "," + status;
    }

    public static Ticket fromCsv(String line) {
        String[] parts = line.split(",");
        if (parts[0].equalsIgnoreCase("ticketId")) {
            return null;
        }
        try {
            String ticketId = parts[0];
            String matchId = parts[1];
            String seatId = parts[2];
            String seatType = parts[3];
            double price = Double.parseDouble(parts[4]);
            String date = parts[5];
            TicketStatus status = TicketStatus.valueOf(parts[6].toUpperCase());
            return new Ticket(ticketId, matchId, seatId, seatType, price, date, status);
        } catch (Exception e) {
            return null; // ignore invalid lines silently
        }
    }
}
