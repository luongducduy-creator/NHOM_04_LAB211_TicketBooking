package model.fan;

import java.util.Objects;

public class Fan {

    private String id;
    private String name;
    private String email;
    private String phone;
    private int birthYear;

    public Fan(String id, String name, String email, String phone, int birthYear) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthYear = birthYear;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    // CSV
    public String toCSV() {
        return id + "," + name + "," + email + "," + phone + "," + birthYear;
    }

    // debug
    @Override
    public String toString() {
        return id + " - " + name + " - " + email + " - " + phone + " - " + birthYear;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Fan))
            return false;

        Fan f = (Fan) o;
        return id.equals(f.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}