package repository;

import model.fan.Fan;

import java.io.*;
import java.util.ArrayList;
import java.util.function.Predicate;

public class FanRepository {
    private final String FILE_NAME = "data/fans.csv";

    // CREATE
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

    // READ ALL
    public ArrayList<Fan> getAllFans() {

        ArrayList<Fan> list = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(FILE_NAME));

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                Fan fan = new Fan(
                        data[0],
                        data[1],
                        data[2]);

                list.add(fan);
            }

            br.close();

        } catch (Exception e) {

            System.out.println("Loi doc file");
        }

        return list;
    }

    // READ BY ID
    public Fan findById(String id) {

        ArrayList<Fan> fans = getAllFans();

        for (Fan fan : fans) {

            if (fan.getId().equals(id)) {

                return fan;
            }
        }

        return null;
    }

    // UPDATE
    public void updateFan(Fan newFan) {

        ArrayList<Fan> fans = getAllFans();

        try {

            FileWriter fw = new FileWriter(FILE_NAME);

            for (Fan fan : fans) {

                if (fan.getId().equals(newFan.getId())) {

                    fw.write(newFan.toString());

                } else {

                    fw.write(fan.toString());
                }

                fw.write("\n");
            }

            fw.close();

        } catch (Exception e) {

            System.out.println("Loi cap nhat fan");
        }
    }

    // DELETE
    public void deleteFan(String id) {

        ArrayList<Fan> fans = getAllFans();

        try {

            FileWriter fw = new FileWriter(FILE_NAME);

            for (Fan fan : fans) {

                if (!fan.getId().equals(id)) {

                    fw.write(fan.toString());
                    fw.write("\n");
                }
            }

            fw.close();

        } catch (Exception e) {

            System.out.println("Loi xoa fan");
        }
    }

    // FIND BY CONDITION
    public ArrayList<Fan> findByCondition(
            Predicate<Fan> condition) {

        ArrayList<Fan> result = new ArrayList<>();

        ArrayList<Fan> fans = getAllFans();

        for (Fan fan : fans) {

            if (condition.test(fan)) {

                result.add(fan);
            }
        }

        return result;
    }
}
