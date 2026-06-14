package test.ticket;

import model.ticket.Ticket;
import model.ticket.TicketStatus;
import repository.TicketRepository;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TicketRepositoryTest {

    @Test
    public void testSaveAndFind() {
        String tempFile = "test_tickets.csv";
        new File(tempFile).delete();

        TicketRepository repo = new TicketRepository(tempFile);

        Ticket t1 = new Ticket("T1", "M1", "SEAT1", "VIP", 800000, "2026-07-10", TicketStatus.AVAILABLE);
        Ticket t2 = new Ticket("T2", "M1", "SEAT2", "NORMAL", 300000, "2026-07-10", TicketStatus.SOLD);

        repo.save(t1);
        repo.save(t2);

        List<Ticket> all = repo.findAll();
        assertEquals(2, all.size());

        List<Ticket> sold = repo.findByStatus("SOLD");
        assertEquals(1, sold.size());
        assertEquals("T2", sold.get(0).getTicketId());

        new File(tempFile).delete();
    }

    @Test
    public void testUpdateTicket() {
        String tempFile = "test_tickets.csv";
        new File(tempFile).delete();

        TicketRepository repo = new TicketRepository(tempFile);

        Ticket t1 = new Ticket("T3", "M2", "SEAT3", "NORMAL", 300000, "2026-07-11", TicketStatus.AVAILABLE);
        repo.save(t1);

        t1.setStatus(TicketStatus.SOLD);
        repo.update(t1);

        Ticket updated = repo.findById("T3");
        assertEquals(TicketStatus.SOLD, updated.getStatus());

        new File(tempFile).delete();
    }

    // Chạy test thủ công nếu chưa cài JUnit
    public static void main(String[] args) {
        TicketRepositoryTest test = new TicketRepositoryTest();
        test.testSaveAndFind();
        test.testUpdateTicket();
        System.out.println("✅ Test chạy xong!");
    }
}
