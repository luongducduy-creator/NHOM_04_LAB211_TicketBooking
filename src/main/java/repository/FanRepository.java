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
        // Keep cleaned copy up‑to‑date
        saveCleanedCopy();
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

                Fan fan = Fan.fromCsvLine(line);
                if (fan != null) {
                    list.add(fan);
                }
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
            // Write CSV header to keep file structure intact
            fw.write("fanId,fullName,email,phone,birthYear,password");
            fw.write(System.lineSeparator());

            java.util.HashSet<String> writtenIds = new java.util.HashSet<>();
            for (Fan f : fans) {
                if (writtenIds.contains(f.getId())) {
                    continue; // skip duplicate entries
                }
                String line = f.getId().equals(newFan.getId()) ? newFan.toCSV() : f.toCSV();
                fw.write(line);
                fw.write(System.lineSeparator());
                writtenIds.add(f.getId());
            }
        } catch (Exception e) {
            System.out.println("Loi update");
        }
        // Keep cleaned copy up‑to‑date after update
        saveCleanedCopy();
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
        // Keep cleaned copy up‑to‑date after deletion
        saveCleanedCopy();
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
    /**
     * Write a cleaned version of the CSV (quotes removed) to
     * <project_root>/data/fans_cleaned.tmp. This method is called after
     * any modification (add / update / delete) to keep the cleaned copy in
     * sync with the main file.
     */
    private void saveCleanedCopy() {
        String cleanedPath = System.getProperty("user.dir") + "/data/fans_cleaned.tmp";
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
             BufferedWriter bw = new BufferedWriter(new FileWriter(cleanedPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove surrounding double quotes from each field
                String cleaned = line.replaceAll("\"", "");
                bw.write(cleaned);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[WARN] Could not write cleaned fans file: " + e.getMessage());
        }
    }
}