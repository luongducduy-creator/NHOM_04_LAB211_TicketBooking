package repository;

import model.Match;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class MatchRepository {
    private String filePath;

    public MatchRepository(String filePath) {
        this.filePath = filePath;
    }

    // Đọc tất cả trận đấu từ CSV
    public List<Match> findAll() {
        List<Match> matches = new ArrayList<>();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Match m = new Match(
                        parts[0],                // matchId
                        parts[1],                // homeTeam
                        parts[2],                // awayTeam
                        LocalDate.parse(parts[3]), // date
                        parts[4]                 // stadiumId
                    );
                    matches.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    // Lưu toàn bộ danh sách trận đấu vào CSV
    public void saveAll(List<Match> matches) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Match m : matches) {
                bw.write(m.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tìm theo điều kiện
    public List<Match> findByCondition(java.util.function.Predicate<Match> condition) {
        return findAll().stream()
                        .filter(condition)
                        .toList();
    }
}
