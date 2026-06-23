package com.nhom04.ticketbooking.model.fan;

import com.nhom04.ticketbooking.model.base.BaseEntity;

/**
 * Entity Fan - biểu diễn một người hâm mộ.
 */
public class Fan extends BaseEntity {
    private String name;
    private String email;
    private String phone;

    public Fan() {}

    public Fan(String id, String name, String email, String phone) {
        super(id);
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getter & Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toCsvLine() {
        return String.join(",", getId(), name, email, phone);
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",");
        setId(parts[0]);
        this.name = parts[1];
        this.email = parts[2];
        this.phone = parts[3];
    }
}
