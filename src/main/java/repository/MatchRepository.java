package repository;

import model.match.Match;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Match entity – reads from matches.csv
 */
public class MatchRepository {

    private final String filePath;

    public MatchRepository() {
        this.filePath = System.getProperty("user.dir") + "/data/matches.csv";
    }

    public MatchRepository(String filePath) {
        this.filePath = filePath;
    }

    public List<Match> findAll() {
        List<Match> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Match m = Match.fromCsvLine(line);
                if (m != null) list.add(m);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot read matches: " + e.getMessage());
        }
        return list;
    }

    public Match findById(String matchId) {
        return findAll().stream()
                .filter(m -> m.getMatchId().equalsIgnoreCase(matchId))
                .findFirst().orElse(null);
    }

    public List<Match> findByStadium(String stadiumId) {
        List<Match> result = new ArrayList<>();
        for (Match m : findAll()) {
            if (m.getStadiumId().equalsIgnoreCase(stadiumId)) result.add(m);
        }
        return result;
    }

    public void saveAll(List<Match> matches) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("matchId,homeTeam,awayTeam,date,stadiumId");
            bw.newLine();
            for (Match m : matches) {
                bw.write(m.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot save matches: " + e.getMessage());
        }
    }

    public void addMatch(Match match) {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(match.toCsvLine());
            fw.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot add match: " + e.getMessage());
        }
    }
}
