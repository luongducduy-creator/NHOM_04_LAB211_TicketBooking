package com.nhom04.ticketbooking.repository;

import com.nhom04.ticketbooking.model.fan.Fan;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository quản lý dữ liệu Fan từ CSV.
 */
public class FanRepository extends CsvRepository<Fan> {

    public FanRepository(String filePath) {
        super(filePath, Fan::new); // Fan có constructor rỗng để fromCsvLine
    }

    // Tìm tất cả fan
    public List<Fan> findAll() {
        return super.findAll();
    }

    // Lưu danh sách fan
    public void saveAll(List<Fan> fans) {
        super.saveAll(fans);
    }

    // ✅ Thêm method save để lưu 1 fan
    public void save(Fan fan) {
        List<Fan> fans = findAll();
        boolean updated = false;
        for (int i = 0; i < fans.size(); i++) {
            if (fans.get(i).getId().equals(fan.getId())) {
                fans.set(i, fan);
                updated = true;
                break;
            }
        }
        if (!updated) {
            fans.add(fan);
        }
        saveAll(fans);
    }

    // ✅ Thêm method getById để tìm theo ID
    public Fan getById(String id) {
        return findByCondition(f -> f.getId().equals(id))
                .stream()
                .findFirst()
                .orElse(null);
    }

    // ✅ Thêm method getAll để lấy toàn bộ
    public List<Fan> getAll() {
        return findAll();
    }

    // Tìm theo điều kiện (ví dụ theo tên, email)
    public List<Fan> findByCondition(Predicate<Fan> condition) {
        return findAll().stream()
                        .filter(condition)
                        .collect(Collectors.toList());
    }
}
