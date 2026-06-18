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

    // Parse CSV line -> Seat
    public static Seat fromCsvLine(String line) {
        String[] parts = line.split(",");
        return new Seat(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    // Serialize Seat -> CSV line
    public String toCsvLine() {
        return String.join(",", seatId, sectionId, row, number, status);
    }

    // Getters
    public String getSeatId() {
        return seatId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getRow() {
        return row;
    }

    public String getNumber() {
        return number;
    }

    public String getStatus() {
        return status;
    }

}
