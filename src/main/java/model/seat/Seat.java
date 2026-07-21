package model.seat;

public class Seat {
    private String seatId;
    private String sectionId;
    private String row;
    private String number;
    private String status; // AVAILABLE, BOOKED, BROKEN

    public Seat(String seatId, String sectionId, String row, String number, String status) {
        this.seatId = seatId;
        this.sectionId = sectionId;
        this.row = row;
        this.number = number;
        this.status = status;
    }

    // Parse CSV line -> Seat (trả null nếu dòng không hợp lệ hoặc là header)
    public static Seat fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split(",");
        if (parts.length < 5) return null;
        if (parts[0].trim().equalsIgnoreCase("seatId")) return null; // skip header
        return new Seat(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim());
    }

    // Serialize Seat -> CSV line
    public String toCsvLine() {
        return String.join(",", seatId, sectionId, row, number, status);
    }

    // Getter
    public String getSeatId() { return seatId; }
    public String getSectionId() { return sectionId; }
    public String getRow() { return row; }
    public String getNumber() { return number; }
    public String getStatus() { return status; }

    // Setter
    public void setSeatId(String seatId) { this.seatId = seatId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }
    public void setRow(String row) { this.row = row; }
    public void setNumber(String number) { this.number = number; }
    public void setStatus(String status) { this.status = status; }
}
