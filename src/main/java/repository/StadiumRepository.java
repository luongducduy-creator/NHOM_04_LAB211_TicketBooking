package repository;

import model.Stadium;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Stadium entity – reads from stadiums.csv
 */
public class StadiumRepository {

    private final String filePath;

    public StadiumRepository() {
        this.filePath = System.getProperty("user.dir") + "/data/stadiums.csv";
    }

    public StadiumRepository(String filePath) {
        this.filePath = filePath;
    }

    public List<Stadium> findAll() {
        List<Stadium> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Stadium s = Stadium.fromCsvLine(line);
                if (s != null) list.add(s);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot read stadiums: " + e.getMessage());
        }
        return list;
    }

    public Stadium findById(String stadiumId) {
        return findAll().stream()
                .filter(s -> s.getStadiumId().equalsIgnoreCase(stadiumId))
                .findFirst().orElse(null);
    }
}
