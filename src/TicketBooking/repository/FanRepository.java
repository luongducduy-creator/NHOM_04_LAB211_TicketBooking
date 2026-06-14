package repository;

import model.fan.Fan;

import java.io.*;
import java.util.ArrayList;


public class FanRepository {
    private final String FILE_NAME = "data/fans.csv";

    // thêm fan
    public void addFan(Fan fan) {

        try {
            FileWriter fw = new FileWriter(FILE_NAME, true);

            fw.write(fan.toString());
            fw.write("\n");

            fw.close();

        } catch (Exception e) {
            System.out.println("Loi ghi file");
        }
    }

    // đọc tất cả fan
    public ArrayList<Fan> getAllFans() {

        ArrayList<Fan> list = new ArrayList<>();

        try {

            BufferedReader br =
                    new BufferedReader(new FileReader(FILE_NAME));

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                Fan fan = new Fan(
                        data[0],
                        data[1],
                        data[2]
                );

                list.add(fan);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Loi doc file");
        }

        return list;
    }

    // tìm fan theo id
    public Fan findById(String id) {

        ArrayList<Fan> fans = getAllFans();

        for (Fan fan : fans) {

            if (fan.getId().equals(id)) {
                return fan;
            }
        }

        return null;
    }
}
