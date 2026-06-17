package model.fan;

public class Fan {

    private String id;
    private String name;
    private String email;
    private String phone;
    private int birthYear;

    public Fan(String id, String name, String email,
               String phone, int birthYear) {
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

    @Override
    public String toString() {
        return id + "," +
                name + "," +
                email + "," +
                phone + "," +
                birthYear;
    }
}