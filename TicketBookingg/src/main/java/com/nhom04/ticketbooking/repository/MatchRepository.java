package com.nhom04.ticketbooking.repository;

import com.nhom04.ticketbooking.model.match.Match;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository quản lý dữ liệu Match từ CSV.
 */
public class MatchRepository extends CsvRepository<Match> {

    public MatchRepository(String filePath) {
        super(filePath, Match::new); // Match có constructor rỗng để fromCsvLine
    }

    // Tìm tất cả trận đấu
    public List<Match> findAll() {
        return super.findAll();
    }

    // Lưu danh sách trận đấu
    public void saveAll(List<Match> matches) {
        super.saveAll(matches);
    }

    // ✅ Thêm method save để lưu 1 trận đấu
    public void save(Match match) {
        List<Match> matches = findAll();
        // Nếu đã tồn tại ID thì cập nhật, ngược lại thêm mới
        boolean updated = false;
        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i).getId().equals(match.getId())) {
                matches.set(i, match);
                updated = true;
                break;
            }
        }
        if (!updated) {
            matches.add(match);
        }
        saveAll(matches);
    }

    // ✅ Thêm method getById để tìm theo ID
    public Match getById(String id) {
        return findByCondition(m -> m.getId().equals(id))
                .stream()
                .findFirst()
                .orElse(null);
    }

    // ✅ Thêm method getAll để lấy toàn bộ
    public List<Match> getAll() {
        return findAll();
    }

    // Tìm theo điều kiện (ví dụ theo trạng thái, ngày, đội bóng)
    public List<Match> findByCondition(Predicate<Match> condition) {
        return findAll().stream()
                        .filter(condition)
                        .collect(Collectors.toList());
    }
}
