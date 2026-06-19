package repository;

import model.ticket.Ticket;
import model.ticket.TicketStatus;

import java.io.*;
import java.util.*;

public class TicketRepository {

    // Đường dẫn tới file CSV
    private String filePath;
    // Danh sách tất cả vé đã đọc từ file
    private List<Ticket> tickets;

    // Khi tạo repository, đọc dữ liệu từ file luôn
    public TicketRepository(String filePath) {
        this.filePath = filePath;
        this.tickets = new ArrayList<>();
        loadData();
    }

    // Đọc dữ liệu từ file CSV và đưa vào danh sách tickets
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

    // Đếm số vé theo trạng thái (ví dụ: SOLD, AVAILABLE)
    public long countByStatus(TicketStatus status) {
        long count = 0;
        for (Ticket t : tickets) {
            if (t.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    // Tìm vé theo ID (cách cũ, vẫn giữ lại)
    public Ticket findById(String ticketId) {
        for (Ticket t : tickets) {
            if (t.getTicketId().equals(ticketId)) {
                return t;
            }
        }
        return null;
    }

    // 🔥 Hàm tìm kiếm linh hoạt: có thể kết hợp nhiều điều kiện
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

            // Kiểm tra từng điều kiện, nếu có truyền vào thì áp dụng
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

            // Nếu vé thỏa mãn tất cả điều kiện thì thêm vào kết quả
            if (match)
                result.add(t);
        }
        return result;
    }

    // In thông tin vé (có thể dùng cho kết quả searchTickets)
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
}
