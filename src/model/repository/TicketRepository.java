package repository;

import model.ticket.Ticket;
import java.util.List;

public class TicketRepository extends CsvRepository<Ticket> {

    public TicketRepository(String filePath) {
        super(filePath, Ticket::fromCsvLine, Ticket::toCsvLine);
    }

    public List<Ticket> findByStatus(String status) {
        return findByCondition(t -> t.getStatus().name().equalsIgnoreCase(status));
    }

    public List<Ticket> findByType(String type) {
        return findByCondition(t -> t.getType().equalsIgnoreCase(type));
    }

    public List<Ticket> findByMatch(String matchId) {
        return findByCondition(t -> t.getMatchId().equalsIgnoreCase(matchId));
    }

    public Ticket findById(String id) {
        return findByCondition(t -> t.getTicketId().equals(id))
                .stream()
                .findFirst()
                .orElse(null);
    }
}
