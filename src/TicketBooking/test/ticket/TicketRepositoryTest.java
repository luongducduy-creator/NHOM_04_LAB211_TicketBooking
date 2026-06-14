package test.ticket;

import repository.TicketRepository;
import model.ticket.Ticket;
import model.ticket.TicketStatus;

import java.io.File;
import java.util.List;

public class TicketRepositoryTest {
    public static void main(String[] args) {
        String filePath = "tickets.csv";
        File file = new File(filePath);
        if (file.exists())
            file.delete();

        TicketRepository repo = new TicketRepository(filePath);

        repo.save(new Ticket("T1", "Alice", TicketStatus.AVAILABLE));
        repo.save(new Ticket("T2", "Bob", TicketStatus.BOOKED));
        repo.save(new Ticket("T3", "Charlie", TicketStatus.CANCELLED));

        List<Ticket> available = repo.findByStatus(TicketStatus.AVAILABLE);
        System.out.println("Available tickets: " + available.size());

        List<Ticket> booked = repo.findByStatus(TicketStatus.BOOKED);
        System.out.println("Booked tickets: " + booked.size());

        System.out.println("✅ Test chạy xong!");
    }
}
