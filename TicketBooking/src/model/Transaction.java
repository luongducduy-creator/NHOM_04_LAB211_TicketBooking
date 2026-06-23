package model;

import java.time.LocalDateTime;

public class Transaction {
    private String id;
    private String ticketId;
    private String fanId;
    private String date;
    private double amount;
    private TransactionStatus status;

    // Constructor rỗng
    public Transaction() {}

    // Constructor 5 tham số
    public Transaction(String id, String ticketId, String fanId, String date, double amount) {
        this.id = id;
        this.ticketId = ticketId;
        this.fanId = fanId;
        this.date = date;
        this.amount = amount;
        this.status = TransactionStatus.PENDING;
    }

    // Constructor đầy đủ cho test
    public Transaction(String id, String ticketId, String fanId, double amount,
                       LocalDateTime dateTime, TransactionStatus status) {
        this.id = id;
        this.ticketId = ticketId;
        this.fanId = fanId;
        this.amount = amount;
        this.date = dateTime.toString();
        this.status = status;
    }

    // Getter & Setter
    public String getId() { return id; }
    public String getTicketId() { return ticketId; }
    public String getFanId() { return fanId; }
    public String getDate() { return date; }
    public double getAmount() { return amount; }

    public String getFanRef() { return fanId; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}
