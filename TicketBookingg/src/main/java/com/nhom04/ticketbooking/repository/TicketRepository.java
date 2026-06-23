package com.nhom04.ticketbooking.repository;

import com.nhom04.ticketbooking.model.ticket.Ticket;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository quản lý dữ liệu Ticket từ CSV.
 */
public class TicketRepository extends CsvRepository<Ticket> {

    public TicketRepository(String filePath) {
        super(filePath, Ticket::new); // Ticket có constructor rỗng để fromCsvLine
    }

    // Tìm tất cả vé
    public List<Ticket> findAll() {
        return super.findAll();
    }

    // Lưu danh sách vé
    public void saveAll(List<Ticket> tickets) {
        super.saveAll(tickets);
    }

    // ✅ Thêm method save để lưu 1 vé
    public void save(Ticket ticket) {
        List<Ticket> tickets = findAll();
        boolean updated = false;
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getId().equals(ticket.getId())) {
                tickets.set(i, ticket);
                updated = true;
                break;
            }
        }
        if (!updated) {
            tickets.add(ticket);
        }
        saveAll(tickets);
    }

    // ✅ Thêm method getById để tìm theo ID
    public Ticket getById(String id) {
        return findByCondition(t -> t.getId().equals(id))
                .stream()
                .findFirst()
                .orElse(null);
    }

    // ✅ Thêm method getAll để lấy toàn bộ
    public List<Ticket> getAll() {
        return findAll();
    }

    // Tìm theo điều kiện (ví dụ theo matchId, seatId, giá)
    public List<Ticket> findByCondition(Predicate<Ticket> condition) {
        return findAll().stream()
                        .filter(condition)
                        .collect(Collectors.toList());
    }
}
