package TicketBooking.repository;

import TicketBooking.model.fan.Fan;

import java.io.*;
import java.util.ArrayList;
import java.util.function.Predicate;

public class FanRepository {

    private final String FILE_NAME = "data/fans.csv";

    public void addFan(Fan fan) {

        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {

            fw.write(fan.toString());
            fw.write("\n");

        } catch (Exception e) {

            System.out.println("Loi ghi file");
        }
    }

    public ArrayList<Fan> getAllFans() {

        ArrayList<Fan> list = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(FILE_NAME))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length < 5) {
                    continue;
                }

                Fan fan = new Fan(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        Integer.parseInt(data[4]));

                list.add(fan);
            }

        } catch (Exception e) {

            System.out.println("Loi doc file");
        }

        return list;
    }

    public Fan findById(String id) {

        for (Fan fan : getAllFans()) {

            if (fan.getId().equals(id)) {
                return fan;
            }
        }

        return null;
    }

    public void updateFan(Fan newFan) {

        ArrayList<Fan> fans = getAllFans();

        try (FileWriter fw = new FileWriter(FILE_NAME)) {

            for (Fan fan : fans) {

                if (fan.getId().equals(newFan.getId())) {
                    fw.write(newFan.toString());
                } else {
                    fw.write(fan.toString());
                }

                fw.write("\n");
            }

        } catch (Exception e) {

            System.out.println("Loi cap nhat fan");
        }
    }

    public void deleteFan(String id) {

        ArrayList<Fan> fans = getAllFans();

        try (FileWriter fw = new FileWriter(FILE_NAME)) {

            for (Fan fan : fans) {

                if (!fan.getId().equals(id)) {

                    fw.write(fan.toString());
                    fw.write("\n");
                }
            }

        } catch (Exception e) {

            System.out.println("Loi xoa fan");
        }
    }

    public ArrayList<Fan> findByCondition(
            Predicate<Fan> condition) {

        ArrayList<Fan> result = new ArrayList<>();

        for (Fan fan : getAllFans()) {

            if (condition.test(fan)) {
                result.add(fan);
            }
        }

        return result;
    }
}