package model;

public class Ticket {
    private String id;
    private String matchId;
    private String fanId;
    private String type;
    private double price;

    // ✅ Constructor đầy đủ 5 tham số
    public Ticket(String id, String matchId, String fanId, String type, double price) {
        this.id = id;
        this.matchId = matchId;
        this.fanId = fanId;
        this.type = type;
        this.price = price;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public String getFanId() { return fanId; }
    public void setFanId(String fanId) { this.fanId = fanId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // ✅ Alias để khớp với repository
    public String getMatchRef() { return matchId; }
    public String getSeatRef() { return fanId; }

    // ✅ Hỗ trợ CSV nếu cần
    public String toCsvLine() {
        return id + "," + matchId + "," + fanId + "," + type + "," + price;
    }

    public void fromCsvLine(String line) {
        String[] parts = line.split(",");
        this.id = parts[0];
        this.matchId = parts[1];
        this.fanId = parts[2];
        this.type = parts[3];
        this.price = Double.parseDouble(parts[4]);
    }
}
