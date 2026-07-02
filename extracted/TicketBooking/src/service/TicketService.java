package service;

import model.Ticket;
import repository.TicketRepository;
import java.util.List;

public class TicketService {
    private TicketRepository ticketRepo;

    public TicketService(String filePath) {
        this.ticketRepo = new TicketRepository(filePath);
    }

    // Thêm vé mới
    public Ticket addTicket(String id, String matchId, String fanId, String type, double price) {
        Ticket t = new Ticket(id, matchId, fanId, type, price);
        List<Ticket> all = ticketRepo.findAll();
        all.add(t);
        ticketRepo.saveAll(all);
        return t;
    }

    // Lấy toàn bộ vé
    public List<Ticket> getAllTickets() {
        return ticketRepo.findAll();
    }

    // Xóa vé theo ID
    public void deleteTicket(String ticketId) {
        List<Ticket> all = ticketRepo.findAll();
        all.removeIf(t -> t.getId().equals(ticketId));
        ticketRepo.saveAll(all);
    }

    // ✅ Thêm method để phục vụ JUnit Test
    public Ticket getTicketById(String id) {
        return ticketRepo.findAll()
                         .stream()
                         .filter(t -> t.getId().equals(id))
                         .findFirst()
                         .orElse(null);
    }
}
