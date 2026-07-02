package model;

public class Seat extends BaseEntity {
    private String code;
    private boolean available;

    public Seat() {}

    public Seat(String id, String code, boolean available) {
        super(id);
        this.code = code;
        this.available = available;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toCsvLine() {
        return String.join(",", getId(), code, String.valueOf(available));
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",");
        setId(parts[0]);
        this.code = parts[1];
        this.available = Boolean.parseBoolean(parts[2]);
    }
}
