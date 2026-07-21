package repository;

import model.transaction.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repository for Transaction entity – reads/writes transactions.csv
 */
public class TransactionRepository {

    private final String filePath;
    private final AtomicInteger idCounter;

    public TransactionRepository() {
        this.filePath = System.getProperty("user.dir") + "/data/transactions.csv";
        this.idCounter = new AtomicInteger(loadMaxIdFromFile());
    }

    public TransactionRepository(String filePath) {
        this.filePath = filePath;
        this.idCounter = new AtomicInteger(loadMaxIdFromFile());
    }

    /** Đọc file một lần duy nhất lúc khởi tạo để tìm số ID lớn nhất hiện có */
    private int loadMaxIdFromFile() {
        int max = 100000;
        File file = new File(filePath);
        if (!file.exists()) return max;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Transaction t = Transaction.fromCsvLine(line);
                if (t != null) {
                    String digits = t.getTransactionId().replaceAll("[^0-9]", "");
                    try { max = Math.max(max, Integer.parseInt(digits)); } catch (Exception ignored) {}
                }
            }
        } catch (IOException e) {
            System.out.println("[WARN] Cannot read transactions for ID init: " + e.getMessage());
        }
        return max;
    }

    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Transaction t = Transaction.fromCsvLine(line);
                if (t != null) list.add(t);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot read transactions: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> findByFanId(String fanId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : findAll()) {
            if (t.getFanId().equalsIgnoreCase(fanId)) result.add(t);
        }
        return result;
    }

    public List<Transaction> findByTicketId(String ticketId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : findAll()) {
            if (t.getTicketId().equalsIgnoreCase(ticketId)) result.add(t);
        }
        return result;
    }

    public Transaction findById(String transactionId) {
        return findAll().stream()
                .filter(t -> t.getTransactionId().equalsIgnoreCase(transactionId))
                .findFirst().orElse(null);
    }

    /** Append a single new transaction to the CSV */
    public void add(Transaction transaction) {
        File file = new File(filePath);
        boolean needsHeader = !file.exists() || file.length() == 0;
        try (FileWriter fw = new FileWriter(file, true)) {
            if (needsHeader) {
                fw.write("transactionId,ticketId,fanId,amount,paymentMethod,status");
                fw.write(System.lineSeparator());
            }
            fw.write(transaction.toCsvLine());
            fw.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot add transaction: " + e.getMessage());
        }
    }

    /** Overwrite entire file (used for updates like cancellation) */
    public void saveAll(List<Transaction> transactions) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("transactionId,ticketId,fanId,amount,paymentMethod,status");
            bw.newLine();
            for (Transaction t : transactions) {
                bw.write(t.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot save transactions: " + e.getMessage());
        }
    }

    /**
     * Sinh transaction ID tiếp theo một cách thread-safe dùng AtomicInteger.
     * Không đọc lại file → không bao giờ trùng dù nhiều thread gọi đồng thời.
     */
    public String generateNextId() {
        return "TR" + idCounter.incrementAndGet();
    }
}
