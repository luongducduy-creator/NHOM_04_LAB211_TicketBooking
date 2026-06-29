package model.transaction;

import java.util.Objects;

/**
 * Transaction entity – maps to transactions.csv
 * transactionId,ticketId,fanId,amount,paymentMethod,status
 */
public class Transaction {

    public enum PaymentMethod { CASH, ONLINE }
    public enum Status { SUCCESS, CONFIRMED, PENDING, CANCELLED }

    private String transactionId;
    private String ticketId;
    private String fanId;
    private double amount;
    private PaymentMethod paymentMethod;
    private Status status;

    public Transaction(String transactionId, String ticketId, String fanId,
                       double amount, PaymentMethod paymentMethod, Status status) {
        this.transactionId = transactionId;
        this.ticketId      = ticketId;
        this.fanId         = fanId;
        this.amount        = amount;
        this.paymentMethod = paymentMethod;
        this.status        = status;
    }

    // ===== GETTERS =====
    public String        getTransactionId() { return transactionId; }
    public String        getTicketId()      { return ticketId; }
    public String        getFanId()         { return fanId; }
    public double        getAmount()        { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Status        getStatus()        { return status; }

    // ===== SETTERS =====
    public void setStatus(Status status)   { this.status = status; }
    public void setAmount(double amount)   { this.amount = amount; }

    /** CSV format: transactionId,ticketId,fanId,amount,paymentMethod,status */
    public String toCsvLine() {
        return String.join(",",
                transactionId, ticketId, fanId,
                String.valueOf(amount),
                paymentMethod.name(),
                status.name());
    }

    public static Transaction fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split(",");
        if (p.length < 6) return null;
        if (p[0].trim().equalsIgnoreCase("transactionId")) return null;
        try {
            PaymentMethod pm = PaymentMethod.valueOf(p[4].trim().toUpperCase());
            Status st = parseStatus(p[5].trim());
            return new Transaction(
                    p[0].trim(), p[1].trim(), p[2].trim(),
                    Double.parseDouble(p[3].trim()), pm, st);
        } catch (Exception e) {
            return null;
        }
    }

    private static Status parseStatus(String raw) {
        switch (raw.toUpperCase()) {
            case "SUCCESS":   return Status.SUCCESS;
            case "CONFIRMED": return Status.CONFIRMED;
            case "PENDING":   return Status.PENDING;
            case "CANCELLED": return Status.CANCELLED;
            default:          return Status.PENDING;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] Ticket:%s Fan:%s Amount:%.0f %s [%s]",
                transactionId, ticketId, fanId, amount, paymentMethod, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction t = (Transaction) o;
        return Objects.equals(transactionId, t.transactionId);
    }

    @Override
    public int hashCode() { return Objects.hash(transactionId); }
}
