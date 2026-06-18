package repository;

import model.ticket.Ticket;
import model.ticket.TicketStatus;

import java.io.*;
import java.util.*;

public class TicketRepository {

    private String filePath;
    private List<Ticket> tickets;

    public TicketRepository(String filePath) {
        this.filePath = filePath;
        this.tickets = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                Ticket ticket = Ticket.fromCsv(line);
                if (ticket != null) {
                    tickets.add(ticket);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long countByStatus(TicketStatus status) {
        long count = 0;
        for (Ticket t : tickets) {
            if (t.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public Ticket findById(String ticketId) {
        for (Ticket t : tickets) {
            if (t.getTicketId().equals(ticketId)) {
                return t;
            }
        }
        return null;
    }

    public void printTicketInfo(String ticketId) {
        Ticket ticket = findById(ticketId);
        if (ticket != null) {
            System.out.println("ID: " + ticket.getTicketId());
            System.out.println("Match: " + ticket.getMatchId());
            System.out.println("Seat: " + ticket.getSeatId());
            System.out.println("SeatType: " + ticket.getSeatType());
            System.out.println("Price: " + ticket.getPrice());
            System.out.println("Date: " + ticket.getDate());
            System.out.println("Status: " + ticket.getStatus());
        } else {
            System.out.println("Không tìm thấy vé với ID: " + ticketId);
        }
    }
}
