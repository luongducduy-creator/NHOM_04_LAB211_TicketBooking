package repository;

import model.ticket.Ticket;
import model.ticket.TicketStatus;

import java.io.*;
import java.util.*;

public class TicketRepository {

    private final String filePath;
    private List<Ticket> cache;

    public TicketRepository(String filePath) {
        this.filePath = filePath;
    }

    // ===== LOAD ONCE =====
    private void loadData() {
        if (cache != null)
            return;

        cache = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = br.readLine()) != null) {

                Ticket ticket = Ticket.fromCsv(line);

                if (ticket != null) {
                    cache.add(ticket);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Ticket> findAll() {
        loadData();
        return cache;
    }

    public List<Ticket> findByStatus(TicketStatus status) {
        loadData();

        List<Ticket> result = new ArrayList<>();

        for (Ticket t : cache) {
            if (t.getStatus() == status) {
                result.add(t);
            }
        }

        return result;
    }

    public long countByStatus(TicketStatus status) {
        loadData();

        long count = 0;
        for (Ticket t : cache) {
            if (t.getStatus() == status) {
                count++;
            }
        }

        return count;
    }
}