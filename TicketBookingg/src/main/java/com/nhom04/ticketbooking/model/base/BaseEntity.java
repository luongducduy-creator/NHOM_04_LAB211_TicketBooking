package com.nhom04.ticketbooking.model.base;

/**
 * BaseEntity - lớp nền tảng cho tất cả entity.
 * Mỗi entity đều có id và phải cài đặt toCsvLine / fromCsvLine.
 */
public abstract class BaseEntity {
    private String id;

    public BaseEntity() {}

    public BaseEntity(String id) {
        this.id = id;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    /**
     * Chuyển entity thành một dòng CSV.
     */
    public abstract String toCsvLine();

    /**
     * Đọc dữ liệu từ một dòng CSV để gán vào entity.
     */
    public abstract void fromCsvLine(String line);
}
