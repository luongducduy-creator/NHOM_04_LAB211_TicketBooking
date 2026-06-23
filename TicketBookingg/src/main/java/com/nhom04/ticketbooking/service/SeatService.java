package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.seat.Seat;
import com.nhom04.ticketbooking.repository.SeatRepository;

import java.util.List;

public class SeatService {
    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    // Thêm ghế mới
    public void addSeat(String id, String code, String zone, int row, boolean available) {
        Seat seat = new Seat(id, code, zone, row, available);
        seatRepository.save(seat);
    }

    // Tìm ghế theo ID
    public Seat findById(String id) {
        return seatRepository.getById(id);
    }

    // Lấy tất cả ghế
    public List<Seat> findAll() {
        return seatRepository.getAll();
    }

    // Cập nhật ghế
    public void updateSeat(Seat seat) {
        seatRepository.save(seat);
    }
}
