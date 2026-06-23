package model.seat;

public class Section {
    private String sectionId;
    private String stadiumId;
    private String name;
    private SeatType type;

    // Constructor
    public Section(String sectionId, String stadiumId, String name, SeatType type) {
        this.sectionId = sectionId;
        this.stadiumId = stadiumId;
        this.name = name;
        this.type = type;
    }

    // Getters
    public String getSectionId() {
        return sectionId;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public String getName() {
        return name;
    }

    public SeatType getType() {
        return type;
    }

    // Setters
    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(SeatType type) {
        this.type = type;
    }

    // CSV Parse / Serialize
    public static Section fromCsvLine(String line) {
        String[] parts = line.split(",");
        return new Section(
            parts[0], 
            parts[1], 
            parts[2], 
            SeatType.fromString(parts[3])
        );
    }

    public String toCsvLine() {
        return String.join(",", sectionId, stadiumId, name, type.name());
    }
}