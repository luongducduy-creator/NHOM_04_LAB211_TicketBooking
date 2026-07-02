package repository;

import model.Transaction;
import model.TransactionStatus;
import java.io.*;
import java.util.*;

public class TransactionRepository {
    private String filePath;

    public TransactionRepository(String filePath) {
        this.filePath = filePath;
    }

    // Lưu danh sách Transaction xuống CSV
    public void saveAll(List<Transaction> transactions) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Transaction t : transactions) {
                pw.println(t.getId() + "," + t.getTicketId() + "," + t.getFanRef() + "," + t.getDate() + "," + t.getAmount() + "," + t.getStatus());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Đọc toàn bộ Transaction từ CSV
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    Transaction t = new Transaction(parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4]));
                    t.setStatus(TransactionStatus.valueOf(parts[5]));
                    list.add(t);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm theo fanId
    public List<Transaction> findByFan(String fanId) {
        return findAll().stream()
                .filter(t -> t.getFanRef().equals(fanId))
                .toList();
    }

    // Tìm theo status
    public List<Transaction> findByStatus(String status) {
        return findAll().stream()
                .filter(t -> t.getStatus().name().equals(status))
                .toList();
    }
}
