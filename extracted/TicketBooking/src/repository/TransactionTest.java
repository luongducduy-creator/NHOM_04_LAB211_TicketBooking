package repository;

import model.Transaction;
import model.TransactionStatus;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionTest {
    public static void main(String[] args) {
        // Khởi tạo repository với file CSV
        TransactionRepository repo = new TransactionRepository("data/transactions.csv");

        // CREATE: thêm transaction mới
        Transaction t1 = new Transaction("TR001", "T001", "F001", 200.0,
                LocalDateTime.now(), TransactionStatus.COMPLETED);

        // Lưu vào file
        repo.saveAll(List.of(t1));

        // READ: đọc toàn bộ file
        List<Transaction> all = repo.findAll();
        System.out.println("Total transactions: " + all.size());

        // UPDATE: sửa transaction
        Transaction t2 = all.get(0);
        t2.setStatus(TransactionStatus.PENDING);
        repo.saveAll(all);

        // DELETE: xóa transaction
        all.remove(0);
        repo.saveAll(all);

        System.out.println("CRUD test completed!");
    }
}
