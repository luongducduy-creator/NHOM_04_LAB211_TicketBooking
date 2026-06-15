package ticketbooking.repository;

import ticketbooking.model.ticket.Ticket;
import ticketbooking.model.ticket.TicketStatus;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class TicketRepository {
    private final String filePath;

    public TicketRepository(String filePath) {
        this.filePath = filePath;
    }

    public List<Ticket> findAll() {
        try {
            return new BufferedReader(new FileReader(filePath))
                    .lines()
                    .map(Ticket::fromCsv)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Cannot read file: " + filePath);
            return new ArrayList<>();
        }
    }

    public List<Ticket> findByStatus(TicketStatus status) {
        return findAll().stream()
                .filter(ticket -> ticket.getStatus() == status)
                .collect(Collectors.toList());
    }
}
