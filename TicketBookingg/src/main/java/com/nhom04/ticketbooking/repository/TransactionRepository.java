package com.nhom04.ticketbooking.repository;

import com.nhom04.ticketbooking.model.transaction.Transaction;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository quản lý dữ liệu Transaction từ CSV.
 */
public class TransactionRepository extends CsvRepository<Transaction> {

    public TransactionRepository(String filePath) {
        super(filePath, Transaction::new); // Transaction có constructor rỗng để fromCsvLine
    }

    // Tìm tất cả giao dịch
    public List<Transaction> findAll() {
        return super.findAll();
    }

    // Lưu danh sách giao dịch
    public void saveAll(List<Transaction> transactions) {
        super.saveAll(transactions);
    }

    // ✅ Thêm method save để lưu 1 giao dịch
    public void save(Transaction transaction) {
        List<Transaction> transactions = findAll();
        boolean updated = false;
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId().equals(transaction.getId())) {
                transactions.set(i, transaction);
                updated = true;
                break;
            }
        }
        if (!updated) {
            transactions.add(transaction);
        }
        saveAll(transactions);
    }

    // ✅ Thêm method getById để tìm theo ID
    public Transaction getById(String id) {
        return findByCondition(t -> t.getId().equals(id))
                .stream()
                .findFirst()
                .orElse(null);
    }

    // ✅ Thêm method getAll để lấy toàn bộ
    public List<Transaction> getAll() {
        return findAll();
    }

    // Tìm theo điều kiện (ví dụ theo ticketId, fanId, trạng thái)
    public List<Transaction> findByCondition(Predicate<Transaction> condition) {
        return findAll().stream()
                        .filter(condition)
                        .collect(Collectors.toList());
    }
}
