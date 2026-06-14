package repository;

import model.ticket.Ticket;
import model.ticket.TicketStatus;

import java.util.List;

public class TicketRepository extends CsvRepository<Ticket> {
    public TicketRepository(String filePath) {
        super(filePath, Ticket::fromCsv, Ticket::toString);
    }

    public List<Ticket> findByStatus(TicketStatus status) {
        return findByCondition(ticket -> ticket.getStatus() == status);
    }
}
