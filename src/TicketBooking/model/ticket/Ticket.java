package model.ticket;

public class Ticket {
    private String id;
    private String customerName;
    private TicketStatus status;

    public Ticket(String id, String customerName, TicketStatus status) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + "," + customerName + "," + status;
    }

    public static Ticket fromCsv(String line) {
        String[] parts = line.split(",");
        // bỏ qua dòng tiêu đề hoặc dòng lỗi
        if (parts[0].equalsIgnoreCase("ticketId") || parts[0].equalsIgnoreCase("id")) {
            return null;
        }
        try {
            return new Ticket(parts[0], parts[1], TicketStatus.valueOf(parts[2].toUpperCase()));
        } catch (Exception e) {
            System.err.println("⚠️ Bỏ qua dòng không hợp lệ: " + line);
            return null;
        }
    }
}
