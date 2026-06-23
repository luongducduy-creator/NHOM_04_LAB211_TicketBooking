package repository;

import model.Ticket;
import java.io.*;
import java.util.*;

public class TicketRepository {
    private String filePath;

    public TicketRepository(String filePath) {
        this.filePath = filePath;
    }

    // Lưu danh sách Ticket xuống CSV
    public void saveAll(List<Ticket> tickets) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Ticket t : tickets) {
                pw.println(t.getId() + "," + t.getMatchRef() + "," + t.getSeatRef() + "," + t.getType() + "," + t.getPrice());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Đọc toàn bộ Ticket từ CSV
    public List<Ticket> findAll() {
        List<Ticket> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    list.add(new Ticket(parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
