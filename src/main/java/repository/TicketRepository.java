package repository;

import model.ticket.Ticket;
import model.ticket.TicketStatus;

import java.io.*;
import java.util.*;

public class TicketRepository {

    private String filePath;
    private List<Ticket> tickets;

    public TicketRepository(String filePath) {
        this.filePath = filePath;
        this.tickets = new ArrayList<>();
        loadData();
    }

    // Đọc dữ liệu từ file CSV
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Ticket ticket = Ticket.fromCsv(line);
                if (ticket != null) {
                    tickets.add(ticket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ghi lại toàn bộ danh sách vé xuống file CSV
    private void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Ticket t : tickets) {
                bw.write(t.toCsv()); // Ticket cần có hàm toCsv()
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Đếm số vé theo trạng thái
    public long countByStatus(TicketStatus status) {
        return tickets.stream().filter(t -> t.getStatus() == status).count();
    }

    // Tìm vé theo ID
    public Ticket findById(String ticketId) {
        for (Ticket t : tickets) {
            if (t.getTicketId().equals(ticketId)) {
                return t;
            }
        }
        return null;
    }

    // Tìm kiếm linh hoạt
    public List<Ticket> searchTickets(
            String ticketId,
            String matchId,
            String seatType,
            Double maxPrice,
            String date,
            TicketStatus status) {

        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets) {
            boolean match = true;

            if (ticketId != null && !t.getTicketId().equals(ticketId))
                match = false;
            if (matchId != null && !t.getMatchId().equals(matchId))
                match = false;
            if (seatType != null && !t.getSeatType().equalsIgnoreCase(seatType))
                match = false;
            if (maxPrice != null && t.getPrice() > maxPrice)
                match = false;
            if (date != null && !t.getDate().equals(date))
                match = false;
            if (status != null && t.getStatus() != status)
                match = false;

            if (match)
                result.add(t);
        }
        return result;
    }

    // In thông tin vé
    public void printTicketInfo(Ticket ticket) {
        if (ticket != null) {
            System.out.println("ID: " + ticket.getTicketId());
            System.out.println("Match: " + ticket.getMatchId());
            System.out.println("Seat: " + ticket.getSeatId());
            System.out.println("SeatType: " + ticket.getSeatType());
            System.out.println("Price: " + ticket.getPrice());
            System.out.println("Date: " + ticket.getDate());
            System.out.println("Status: " + ticket.getStatus());
        } else {
            System.out.println("Không tìm thấy vé!");
        }
    }

    // ✅ Thêm vé mới
    public void addTicket(Ticket newTicket) {
        tickets.add(newTicket);
        saveData(); // cập nhật file CSV
    }

    // ✅ Xóa vé theo ID
    public boolean removeTicket(String ticketId) {
        Iterator<Ticket> iterator = tickets.iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            Ticket t = iterator.next();
            if (t.getTicketId().equals(ticketId)) {
                iterator.remove();
                removed = true;
            }
        }
        if (removed) {
            saveData(); // cập nhật file CSV
        }
        return removed;
    }
}
