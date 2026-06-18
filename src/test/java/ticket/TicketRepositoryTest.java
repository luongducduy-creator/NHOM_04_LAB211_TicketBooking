package ticket;

import repository.TicketRepository;
import model.ticket.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TicketRepositoryTest {

    private final TicketRepository repo = new TicketRepository("data/tickets.csv");

    @Test
    public void testFindAllTickets() {
        assertEquals(
                100000,
                repo.findAll().size());
    }

    @Test
    public void testAvailableTickets() {
        assertEquals(
                49722,
                repo.findByStatus(TicketStatus.AVAILABLE).size());
    }

    @Test
    public void testSoldTickets() {
        assertEquals(
                50278,
                repo.findByStatus(TicketStatus.SOLD).size());
    }
}