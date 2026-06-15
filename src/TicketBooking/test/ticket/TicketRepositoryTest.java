package ticketbooking.test.ticket;

import ticketbooking.repository.TicketRepository;
import ticketbooking.model.ticket.Ticket;
import ticketbooking.model.ticket.TicketStatus;
import java.util.List;

public class TicketRepositoryTest {
    public static void main(String[] args) {
        String filePath = "data/tickets.csv";
        TicketRepository repo = new TicketRepository(filePath);

        List<Ticket> all = repo.findAll();
        System.out.println("Total tickets: " + all.size());

        System.out.println("Available tickets: " + repo.findByStatus(TicketStatus.AVAILABLE).size());
        System.out.println("Sold tickets: " + repo.findByStatus(TicketStatus.SOLD).size());
        System.out.println("Cancelled tickets: " + repo.findByStatus(TicketStatus.CANCELLED).size());
        System.out.println("Test completed successfully!");
    }
}
