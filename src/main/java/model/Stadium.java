package model;

import java.util.Objects;

/**
 * Stadium entity – maps to stadiums.csv
 * stadiumId,name,location,capacity
 */
public class Stadium {

    private String stadiumId;
    private String name;
    private String location;
    private int capacity;

    public Stadium(String stadiumId, String name, String location, int capacity) {
        this.stadiumId = stadiumId;
        this.name      = name;
        this.location  = location;
        this.capacity  = capacity;
    }

    // ===== GETTERS =====
    public String getStadiumId() { return stadiumId; }
    public String getName()      { return name; }
    public String getLocation()  { return location; }
    public int    getCapacity()  { return capacity; }

    // ===== SETTERS =====
    public void setName(String name)         { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity)    { this.capacity = capacity; }

    /** CSV format: stadiumId,name,location,capacity */
    public String toCsvLine() {
        return String.join(",", stadiumId, name, location, String.valueOf(capacity));
    }

    public static Stadium fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split(",");
        if (p.length < 4) return null;
        if (p[0].trim().equalsIgnoreCase("stadiumId")) return null;
        try {
            return new Stadium(p[0].trim(), p[1].trim(), p[2].trim(), Integer.parseInt(p[3].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s – %s (Capacity: %d)", stadiumId, name, location, capacity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stadium)) return false;
        Stadium s = (Stadium) o;
        return Objects.equals(stadiumId, s.stadiumId);
    }

    @Override
    public int hashCode() { return Objects.hash(stadiumId); }
}
