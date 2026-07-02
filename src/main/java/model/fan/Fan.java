package model.fan;

import java.util.Objects;

public class Fan {

    private String id;
    private String name;
    private String email;
    private String phone;
    private int    birthYear;
    private String password;  // Added for login (T5)

    public Fan(String id, String name, String email, String phone, int birthYear, String password) {
        this.id        = id;
        this.name      = name;
        this.email     = email;
        this.phone     = phone;
        this.birthYear = birthYear;
        this.password  = password;
    }

    /** Backward-compat constructor (no password) */
    public Fan(String id, String name, String email, String phone, int birthYear) {
        this(id, name, email, phone, birthYear, "123456");
    }

    // ===== GETTERS =====
    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public int    getBirthYear(){ return birthYear; }
    public String getPassword() { return password; }

    // ===== SETTERS =====
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPhone(String phone)       { this.phone = phone; }
    public void setBirthYear(int birthYear)  { this.birthYear = birthYear; }
    public void setPassword(String password) { this.password = password; }

    /** CSV format: fanId,fullName,email,phone,birthYear,password */
    public String toCSV() {
        // Plain CSV without surrounding quotes
        return id + "," + name + "," + email + "," + phone + "," + birthYear + "," + password;
    }

    public static Fan fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] data = line.split("\\s*,\\s*");
        if (data.length < 5) return null;
        // Remove surrounding quotes from each field if present
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].replaceAll("^\"|\"$", "").trim();
        }
        // Skip header
        if (data[0].equalsIgnoreCase("fanId")) return null;
        String password = (data.length >= 6) ? data[5] : "123456";
        try {
            return new Fan(data[0], data[1], data[2], data[3],
                    Integer.parseInt(data[4]), password);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | %d", id, name, email, phone, birthYear);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fan)) return false;
        Fan fan = (Fan) o;
        return Objects.equals(id, fan.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}