package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.seat.Seat;
import com.nhom04.ticketbooking.repository.SeatRepository;
import com.nhom04.ticketbooking.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeatServiceTest {
    private SeatService seatService;
    private SeatRepository seatRepository;

    @BeforeEach
    void setUp() {
        seatRepository = new SeatRepository("seats_test.csv");
        seatService = new SeatService(seatRepository);
    }

    @Test
    void testAddSeat() {
        seatService.addSeat("S001", "A1", "VIP", 1, true);
        Seat seat = seatService.findById("S001");
        assertNotNull(seat);
        assertEquals("A1", seat.getCode());
        assertEquals("VIP", seat.getZone());
        assertEquals(1, seat.getRow());
        assertTrue(seat.isAvailable());
    }

    @Test
    void testFindById() {
        seatService.addSeat("S002", "A2", "VIP", 1, true);
        Seat seat = seatService.findById("S002");
        assertNotNull(seat);
        assertEquals("A2", seat.getCode());
    }

    @Test
    void testFindAll() {
        seatService.addSeat("S003", "B1", "Standard", 2, true);
        seatService.addSeat("S004", "B2", "Standard", 2, false);
        List<Seat> seats = seatService.findAll();
        assertTrue(seats.size() >= 2);
    }

    @Test
    void testUpdateSeat() {
        seatService.addSeat("S005", "C1", "VIP", 3, true);
        Seat seat = seatService.findById("S005");
        seat.setAvailable(false);
        seatService.updateSeat(seat);

        Seat updated = seatService.findById("S005");
        assertFalse(updated.isAvailable());
    }
}
