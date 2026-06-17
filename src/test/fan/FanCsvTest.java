package test.fan;

import model.fan.Fan;
import repository.FanRepository;

public class FanCsvTest {

    public static void main(String[] args) {

        FanRepository repo = new FanRepository();

        Fan fan = new Fan(
                "F004",
                "Duy",
                "duy@gmail.com",
                "0912345678",
                2004);

        repo.addFan(fan);

        System.out.println("Danh sach fan:");

        for (Fan f : repo.getAllFans()) {

            System.out.println(
                    f.getId() + " - " +
                    f.getName() + " - " +
                    f.getEmail());
        }

        Fan result = repo.findById("F001");

        if (result != null) {

            System.out.println(
                    "Tim thay: " +
                    result.getName());
        }
    }
}