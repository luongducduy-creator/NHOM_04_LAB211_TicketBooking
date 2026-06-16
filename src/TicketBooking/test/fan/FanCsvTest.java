package test.fan;

import model.fan.Fan;
import repository.FanRepository;

public class FanCsvTest {
    public static void main(String[] args) {

        FanRepository repo = new FanRepository();

        // thêm dữ liệu
        Fan fan = new Fan(
                "F004",
                "Duy",
                "duy@gmail.com");

        repo.addFan(fan);

        // hiển thị danh sách
        for (Fan f : repo.getAllFans()) {

            System.out.println(
                    f.getId() + " - " +
                            f.getName() + " - " +
                            f.getEmail());
        }

        // tìm kiếm
        Fan result = repo.findById("F001");

        if (result != null) {
            System.out.println("Tim thay: "
                    + result.getName());
        }
    }
}
