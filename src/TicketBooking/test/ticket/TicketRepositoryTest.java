package test.ticket;

import model.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import repository.TicketRepository;

import static org.junit.jupiter.api.Assertions.*;

public class TicketRepositoryTest {

    private final TicketRepository repo = new TicketRepository("data/tickets.csv");

    @Test
    void testFindAll() {
        assertEquals(22, repo.findAll().size());
    }

    @Test
    void testAvailableTickets() {
        assertEquals(
                14,
                repo.findByStatus(TicketStatus.AVAILABLE).size());
    }

    @Test
    void testSoldTickets() {
        assertEquals(
                8,
                repo.findByStatus(TicketStatus.SOLD).size());
    }

    @Test
    void testCancelledTickets() {
        assertEquals(
                0,
                repo.findByStatus(TicketStatus.CANCELLED).size());
    }
}