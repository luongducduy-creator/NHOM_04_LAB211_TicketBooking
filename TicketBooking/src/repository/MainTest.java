package repository;

import model.Match;
import model.MatchStatus;
import java.time.LocalDate;

public class MainTest {
    public static void main(String[] args) {
        // Tạo một Match mẫu
        Match match = new Match("M001", "TeamA", "TeamB", LocalDate.of(2024, 6, 11), "S001");
        match.setStatus(MatchStatus.SCHEDULED);

        // Ghi ra CSV line
        String csvLine = match.toCsvLine();
        System.out.println("CSV Line: " + csvLine);

        // Đọc lại từ CSV line
        Match parsedMatch = new Match();
        parsedMatch.fromCsvLine(csvLine);

        // In thông tin để kiểm tra
        System.out.println("MatchId: " + parsedMatch.getMatchId());
        System.out.println("HomeTeam: " + parsedMatch.getHomeTeam());
        System.out.println("AwayTeam: " + parsedMatch.getAwayTeam());
        System.out.println("Date: " + parsedMatch.getDate());
        System.out.println("Stadium: " + parsedMatch.getStadiumId());
        System.out.println("Status: " + parsedMatch.getStatus());

        // So sánh dữ liệu
        if (match.getMatchId().equals(parsedMatch.getMatchId())
                && match.getHomeTeam().equals(parsedMatch.getHomeTeam())
                && match.getAwayTeam().equals(parsedMatch.getAwayTeam())) {
            System.out.println("CSV parse test PASSED!");
        } else {
            System.out.println("CSV parse test FAILED!");
        }
    }
}
