package repository;

import model.fan.Fan;

import java.io.*;
import java.util.ArrayList;
import java.util.function.Predicate;

public class FanRepository {

    private final String FILE_NAME;

    // APP thật
    public FanRepository() {
        this.FILE_NAME = System.getProperty("user.dir") + "/data/fans.csv";
    }

    // TEST file tạm
    public FanRepository(String fileName) {
        this.FILE_NAME = fileName;
    }

    public void addFan(Fan fan) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(fan.toCSV());
            fw.write(System.lineSeparator());
        } catch (Exception e) {
            System.out.println("Loi ghi file");
        }
    }

    public ArrayList<Fan> getAllFans() {

        ArrayList<Fan> list = new ArrayList<>();

        File file = new File(FILE_NAME);

        // ✔ FIX: nếu file chưa có thì tạo luôn
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Loi tao file");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty())
                    continue; // ✔ FIX dòng rỗng

                String[] data = line.split("\\s*,\\s*");

                if (data.length < 5)
                    continue;

                list.add(new Fan(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        Integer.parseInt(data[4])));
            }

        } catch (Exception e) {
            System.out.println("Loi doc file");
        }

        return list;
    }

    public Fan findById(String id) {
        for (Fan f : getAllFans()) {
            if (f.getId().equals(id))
                return f;
        }
        return null;
    }

    public void updateFan(Fan newFan) {

        ArrayList<Fan> fans = getAllFans();

        try (FileWriter fw = new FileWriter(FILE_NAME)) {

            for (Fan f : fans) {

                fw.write(
                        f.getId().equals(newFan.getId())
                                ? newFan.toCSV()
                                : f.toCSV());

                fw.write(System.lineSeparator());
            }

        } catch (Exception e) {
            System.out.println("Loi update");
        }
    }

    public void deleteFan(String id) {

        ArrayList<Fan> fans = getAllFans();

        try (FileWriter fw = new FileWriter(FILE_NAME)) {

            for (Fan f : fans) {

                if (!f.getId().equals(id)) {
                    fw.write(f.toCSV());
                    fw.write(System.lineSeparator());
                }
            }

        } catch (Exception e) {
            System.out.println("Loi delete");
        }
    }

    public ArrayList<Fan> findByCondition(Predicate<Fan> condition) {

        ArrayList<Fan> result = new ArrayList<>();

        for (Fan f : getAllFans()) {
            if (condition.test(f)) {
                result.add(f);
            }
        }

        return result;
    }
}