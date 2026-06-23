package com.nhom04.ticketbooking.repository;

import com.nhom04.ticketbooking.model.seat.Seat;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository quản lý dữ liệu Seat từ CSV.
 */
public class SeatRepository extends CsvRepository<Seat> {

    public SeatRepository(String filePath) {
        super(filePath, Seat::new); // Seat có constructor rỗng để fromCsvLine
    }

    // Tìm tất cả ghế
    public List<Seat> findAll() {
        return super.findAll();
    }

    // Lưu danh sách ghế
    public void saveAll(List<Seat> seats) {
        super.saveAll(seats);
    }

    // ✅ Thêm method save để lưu 1 ghế
    public void save(Seat seat) {
        List<Seat> seats = findAll();
        boolean updated = false;
        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i).getId().equals(seat.getId())) {
                seats.set(i, seat);
                updated = true;
                break;
            }
        }
        if (!updated) {
            seats.add(seat);
        }
        saveAll(seats);
    }

    // ✅ Thêm method getById để tìm theo ID
    public Seat getById(String id) {
        return findByCondition(s -> s.getId().equals(id))
                .stream()
                .findFirst()
                .orElse(null);
    }

    // ✅ Thêm method getAll để lấy toàn bộ
    public List<Seat> getAll() {
        return findAll();
    }

    // Tìm theo điều kiện (ví dụ theo zone, row, trạng thái)
    public List<Seat> findByCondition(Predicate<Seat> condition) {
        return findAll().stream()
                        .filter(condition)
                        .collect(Collectors.toList());
    }
}
