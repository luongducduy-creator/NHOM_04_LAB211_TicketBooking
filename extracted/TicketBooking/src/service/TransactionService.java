package service;

import model.Transaction;
import repository.TransactionRepository;
import java.util.List;

public class TransactionService {
    private TransactionRepository repo;

    public TransactionService(String filePath) {
        this.repo = new TransactionRepository(filePath);
    }

    // Thêm transaction mới
    public Transaction addTransaction(String id, String ticketId, String fanId, String date, double amount) {
        Transaction t = new Transaction(id, ticketId, fanId, date, amount);
        List<Transaction> all = repo.findAll();
        all.add(t);
        repo.saveAll(all);
        return t;
    }

    // ✅ Đọc toàn bộ transaction
    public List<Transaction> findAll() {
        return repo.findAll();
    }

    // ✅ Lưu transaction (update hoặc insert)
    public void saveTransaction(Transaction t) {
        List<Transaction> all = repo.findAll();
        // Nếu đã tồn tại thì update, nếu chưa thì thêm mới
        all.removeIf(x -> x.getId().equals(t.getId()));
        all.add(t);
        repo.saveAll(all);
    }

    // ✅ Tìm transaction theo id
    public Transaction findById(String id) {
        return repo.findAll().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
