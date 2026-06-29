package repository;

import model.seat.Section;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Section entity – reads from sections.csv
 */
public class SectionRepository {

    private final String filePath;

    public SectionRepository() {
        this.filePath = System.getProperty("user.dir") + "/data/sections.csv";
    }

    public SectionRepository(String filePath) {
        this.filePath = filePath;
    }

    public List<Section> findAll() {
        List<Section> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.startsWith("sectionId")) continue;
                try {
                    Section s = Section.fromCsvLine(line);
                    if (s != null) list.add(s);
                } catch (Exception e) {
                    // Skip malformed lines
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot read sections: " + e.getMessage());
        }
        return list;
    }

    public List<Section> findByStadium(String stadiumId) {
        List<Section> result = new ArrayList<>();
        for (Section s : findAll()) {
            if (s.getStadiumId().equalsIgnoreCase(stadiumId)) result.add(s);
        }
        return result;
    }

    public Section findById(String sectionId) {
        return findAll().stream()
                .filter(s -> s.getSectionId().equalsIgnoreCase(sectionId))
                .findFirst().orElse(null);
    }
}
