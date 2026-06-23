package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.ticket.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceTest {
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketService("tickets_test.csv");
    }

    @Test
    void testAddTicket() {
        ticketService.addTicket("T001", "M001", "S001", "F001", 100.0);
        Ticket ticket = ticketService.findById("T001");
        assertNotNull(ticket);
        assertEquals("M001", ticket.getMatchId());
        assertEquals("S001", ticket.getSeatId());
        assertEquals("F001", ticket.getFanId());
        assertEquals(100.0, ticket.getPrice());
    }

    @Test
    void testFindById() {
        ticketService.addTicket("T002", "M002", "S002", "F002", 150.0);
        Ticket ticket = ticketService.findById("T002");
        assertNotNull(ticket);
        assertEquals("M002", ticket.getMatchId());
    }

    @Test
    void testFindAll() {
        ticketService.addTicket("T003", "M003", "S003", "F003", 120.0);
        ticketService.addTicket("T004", "M004", "S004", "F004", 130.0);
        List<Ticket> tickets = ticketService.findAll();
        assertTrue(tickets.size() >= 2);
    }

    @Test
    void testUpdateTicket() {
        ticketService.addTicket("T005", "M005", "S005", "F005", 200.0);
        Ticket ticket = ticketService.findById("T005");
        ticket.setPrice(250.0);
        ticketService.updateTicket(ticket);

        Ticket updated = ticketService.findById("T005");
        assertEquals(250.0, updated.getPrice());
    }
}
