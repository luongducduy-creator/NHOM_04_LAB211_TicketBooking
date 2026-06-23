package service;

import model.Match;
import model.MatchStatus;
import repository.MatchRepository;
import java.time.LocalDate;
import java.util.List;

public class MatchService {
    private MatchRepository matchRepo;

    public MatchService(String filePath) {
        this.matchRepo = new MatchRepository(filePath);
    }

    // Thêm trận đấu mới
    public Match addMatch(String id, String homeTeam, String awayTeam, LocalDate date, String stadiumId) {
        Match m = new Match(id, homeTeam, awayTeam, date, stadiumId);
        List<Match> all = matchRepo.findAll();
        all.add(m);
        matchRepo.saveAll(all);
        return m;
    }

    // Cập nhật trạng thái trận đấu
    public void updateStatus(String matchId, MatchStatus status) {
        List<Match> all = matchRepo.findAll();
        for (Match m : all) {
            if (m.getId().equals(matchId)) {
                m.setStatus(status);
            }
        }
        matchRepo.saveAll(all);
    }

    // Tìm trận theo ngày
    public List<Match> findByDate(LocalDate date) {
        return matchRepo.findByCondition(m -> m.getDate().equals(date));
    }

    // Lấy toàn bộ trận đấu
    public List<Match> getAllMatches() {
        return matchRepo.findAll();
    }

    // Xóa trận đấu theo ID
    public void deleteMatch(String matchId) {
        List<Match> all = matchRepo.findAll();
        all.removeIf(m -> m.getId().equals(matchId));
        matchRepo.saveAll(all);
    }

    // ✅ Thêm method để phục vụ JUnit Test
    public Match getMatchById(String id) {
        return matchRepo.findAll()
                        .stream()
                        .filter(m -> m.getId().equals(id))
                        .findFirst()
                        .orElse(null);
    }
}
