package com.nhom04.ticketbooking.model.transaction;

import com.nhom04.ticketbooking.model.base.BaseEntity;
import java.time.LocalDateTime;

public class Transaction extends BaseEntity {
    private String ticketId;
    private String fanId;
    private String date; // lưu dạng String cho khớp với service
    private double amount;
    private LocalDateTime dateTime;
    private TransactionStatus status;

    // ✅ Constructor mặc định (no-args)
    public Transaction() {}

    // Constructor đầy đủ 5 tham số (dùng trong service)
    public Transaction(String id, String ticketId, String fanId, String date, double amount) {
        super(id);
        this.ticketId = ticketId;
        this.fanId = fanId;
        this.date = date;
        this.amount = amount;
    }

    // Constructor đầy đủ cho test TransactionTest
    public Transaction(String id, String ticketId, String fanId, double amount,
                       LocalDateTime dateTime, TransactionStatus status) {
        super(id);
        this.ticketId = ticketId;
        this.fanId = fanId;
        this.amount = amount;
        this.dateTime = dateTime;
        this.status = status;
    }

    // Getter & Setter
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getFanId() { return fanId; }
    public void setFanId(String fanId) { this.fanId = fanId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    // ✅ Override BaseEntity methods
    @Override
    public String toCsvLine() {
        return String.join(",",
                getId(),
                ticketId,
                fanId,
                date != null ? date : "",
                String.valueOf(amount),
                dateTime != null ? dateTime.toString() : "",
                status != null ? status.name() : "");
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 7) {
            setId(parts[0]);
            this.ticketId = parts[1];
            this.fanId = parts[2];
            this.date = parts[3];
            this.amount = Double.parseDouble(parts[4]);
            this.dateTime = parts[5].isEmpty() ? null : LocalDateTime.parse(parts[5]);
            this.status = parts[6].isEmpty() ? null : TransactionStatus.valueOf(parts[6]);
        }
    }
}
