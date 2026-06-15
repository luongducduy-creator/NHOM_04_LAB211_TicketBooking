package test.ticket;

import repository.TicketRepository;
import model.ticket.Ticket;
import model.ticket.TicketStatus;

import java.util.List;

public class TicketRepositoryTest {
    public static void main(String[] args) {
        // trỏ tới file CSV test trong thư mục data
        String filePath = "data/tickets.csv";

        TicketRepository repo = new TicketRepository(filePath);

        List<Ticket> all = repo.findAll();
        System.out.println("Tổng số ticket: " + all.size());

        List<Ticket> available = repo.findByStatus(TicketStatus.AVAILABLE);
        System.out.println("Available tickets: " + available.size());

        List<Ticket> booked = repo.findByStatus(TicketStatus.BOOKED);
        System.out.println("Booked tickets: " + booked.size());

        List<Ticket> cancelled = repo.findByStatus(TicketStatus.CANCELLED);
        System.out.println("Cancelled tickets: " + cancelled.size());

        System.out.println("✅ Test chạy xong!");
    }
}
