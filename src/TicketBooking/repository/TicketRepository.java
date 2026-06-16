package repository;

import model.ticket.Ticket;
import model.ticket.TicketStatus;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TicketRepository {

    private final String filePath;

    public TicketRepository(String filePath) {
        this.filePath = filePath;
    }

    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = br.readLine()) != null) {
                Ticket ticket = Ticket.fromCsv(line);

                if (ticket != null) {
                    tickets.add(ticket);
                }
            }

        } catch (IOException e) {
            System.err.println("Cannot read file: " + filePath);
            e.printStackTrace();
        }

        return tickets;
    }

    public List<Ticket> findByStatus(TicketStatus status) {
        return findAll().stream()
                .filter(ticket -> ticket.getStatus() == status)
                .collect(Collectors.toList());
    }
}