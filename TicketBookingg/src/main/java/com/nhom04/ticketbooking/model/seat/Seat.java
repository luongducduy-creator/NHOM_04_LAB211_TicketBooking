package com.nhom04.ticketbooking.model.seat;

import com.nhom04.ticketbooking.model.base.BaseEntity;

/**
 * Entity Seat - biểu diễn một ghế ngồi.
 */
public class Seat extends BaseEntity {
    private String code;       // mã ghế (ví dụ A1, B2)
    private String zone;       // khu vực (ví dụ VIP, Standard)
    private int row;           // số hàng
    private boolean available; // trạng thái còn trống

    public Seat() {}

    public Seat(String id, String code, String zone, int row, boolean available) {
        super(id);
        this.code = code;
        this.zone = zone;
        this.row = row;
        this.available = available;
    }

    // Getter & Setter
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toCsvLine() {
        return String.join(",", getId(), code, zone, String.valueOf(row), String.valueOf(available));
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",");
        setId(parts[0]);
        this.code = parts[1];
        this.zone = parts[2];
        this.row = Integer.parseInt(parts[3]);
        this.available = Boolean.parseBoolean(parts[4]);
    }
}
