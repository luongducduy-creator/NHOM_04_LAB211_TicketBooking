package repository;

import model.invoice.Invoice;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InvoiceRepository {
    private final String filePath;
    private final AtomicInteger idCounter;

    public InvoiceRepository() {
        this(System.getProperty("user.dir") + "/data/invoices.csv");
    }

    public InvoiceRepository(String filePath) {
        this.filePath = filePath;
        initFile();
        this.idCounter = new AtomicInteger(loadMaxIdFromFile());
    }

    private int loadMaxIdFromFile() {
        int max = 1000;
        File file = new File(filePath);
        if (!file.exists()) return max;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Invoice inv = Invoice.fromCsvLine(line);
                if (inv != null) {
                    String digits = inv.getInvoiceId().replaceAll("[^0-9]", "");
                    if (!digits.isEmpty()) {
                        try { max = Math.max(max, Integer.parseInt(digits)); } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[WARN] Cannot read invoices for ID init: " + e.getMessage());
        }
        return max;
    }

    private void initFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write("invoiceId,bookingId,totalAmount,issuedDate");
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("[ERROR] Cannot initialize invoices file: " + e.getMessage());
            }
        }
    }

    public List<Invoice> findAll() {
        List<Invoice> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists())
            return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Invoice inv = Invoice.fromCsvLine(line);
                if (inv != null)
                    list.add(inv);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot read invoices: " + e.getMessage());
        }
        return list;
    }

    public void saveAll(List<Invoice> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("invoiceId,bookingId,totalAmount,issuedDate");
            bw.newLine();
            for (Invoice inv : list) {
                bw.write(inv.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot save invoices: " + e.getMessage());
        }
    }

    public void addInvoice(Invoice invoice) {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(invoice.toCsvLine());
            fw.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot add invoice: " + e.getMessage());
        }
    }

    public String generateNextInvoiceId() {
        return "INV" + idCounter.incrementAndGet();
    }
}
