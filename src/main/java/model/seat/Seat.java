package model.seat;

public class Seat {
    private String seatId;
    private String sectionId;
    private String row;
    private String number;
    private String status; // AVAILABLE, LOCKED, BOOKED, BROKEN
    private int version;

    public Seat(String seatId, String sectionId, String row, String number, String status) {
        this(seatId, sectionId, row, number, status, 0);
    }

    public Seat(String seatId, String sectionId, String row, String number, String status, int version) {
        this.seatId = seatId;
        this.sectionId = sectionId;
        this.row = row;
        this.number = number;
        this.status = status;
        this.version = version;
    }

    // Parse CSV line -> Seat
    public static Seat fromCsvLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid seat CSV row: " + line);
        }
        int parsedVersion = 0;
        if (parts.length >= 6 && !parts[5].isBlank()) {
            try {
                parsedVersion = Integer.parseInt(parts[5].trim());
            } catch (NumberFormatException ignored) {
                // Legacy rows and headers default to version 0.
            }
        }
        return new Seat(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                parts[3].trim(), parts[4].trim(), parsedVersion);
    }

    // Serialize Seat -> CSV line
    public String toCsvLine() {
        return String.join(",", seatId, sectionId, row, number, status, String.valueOf(version));
    }

    // Getter
    public String getSeatId() { return seatId; }
    public String getSectionId() { return sectionId; }
    public String getRow() { return row; }
    public String getNumber() { return number; }
    public String getStatus() { return status; }
    public int getVersion() { return version; }

    // Setter
    public void setSeatId(String seatId) { this.seatId = seatId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }
    public void setRow(String row) { this.row = row; }
    public void setNumber(String number) { this.number = number; }
    public void setStatus(String status) { this.status = status; }
    public void setVersion(int version) { this.version = version; }
}
