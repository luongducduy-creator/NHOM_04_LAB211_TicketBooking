package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.ticket.Ticket;
import com.nhom04.ticketbooking.repository.TicketRepository;

import java.util.List;

public class TicketService {
    private final TicketRepository ticketRepository;

    // Constructor nhận filePath
    public TicketService(String filePath) {
        this.ticketRepository = new TicketRepository(filePath);
    }

    // Thêm ticket mới (5 tham số: id, matchId, seatId, fanId, price)
    public void addTicket(String id, String matchId, String seatId, String fanId, double price) {
        Ticket ticket = new Ticket(id, matchId, seatId, fanId, price);
        ticketRepository.save(ticket);
    }

    // Tìm ticket theo ID
    public Ticket findById(String id) {
        return ticketRepository.getById(id);
    }

    // Lấy tất cả ticket
    public List<Ticket> findAll() {
        return ticketRepository.getAll();
    }

    // Cập nhật ticket
    public void updateTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }
}
