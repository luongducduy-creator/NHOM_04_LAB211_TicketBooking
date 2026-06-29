package model.invoice;

public class Invoice {
    private String invoiceId;
    private String bookingId;
    private double totalAmount;
    private String issuedDate;

    public Invoice(String invoiceId, String bookingId, double totalAmount, String issuedDate) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.issuedDate = issuedDate;
    }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }

    public String toCsvLine() {
        return String.join(",", invoiceId, bookingId, String.valueOf(totalAmount), issuedDate);
    }

    public static Invoice fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split(",");
        if (parts.length < 4) return null;
        if (parts[0].trim().equalsIgnoreCase("invoiceId")) return null;
        try {
            return new Invoice(
                parts[0].trim(),
                parts[1].trim(),
                Double.parseDouble(parts[2].trim()),
                parts[3].trim()
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
