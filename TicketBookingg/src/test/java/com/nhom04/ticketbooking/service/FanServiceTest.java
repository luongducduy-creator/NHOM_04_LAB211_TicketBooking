package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.fan.Fan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FanServiceTest {
    private FanService fanService;

    @BeforeEach
    void setUp() {
        fanService = new FanService("fans_test.csv");
    }

    @Test
    void testAddFan() {
        fanService.addFan("F001", "Nguyen Van A", "a@example.com", "0123456789");
        Fan fan = fanService.findById("F001");
        assertNotNull(fan);
        assertEquals("Nguyen Van A", fan.getName());
        assertEquals("a@example.com", fan.getEmail());
        assertEquals("0123456789", fan.getPhone());
    }

    @Test
    void testFindById() {
        fanService.addFan("F002", "Tran Thi B", "b@example.com", "0987654321");
        Fan fan = fanService.findById("F002");
        assertNotNull(fan);
        assertEquals("Tran Thi B", fan.getName());
    }

    @Test
    void testFindAll() {
        fanService.addFan("F003", "Le Van C", "c@example.com", "0111111111");
        fanService.addFan("F004", "Pham Thi D", "d@example.com", "0222222222");
        List<Fan> fans = fanService.findAll();
        assertTrue(fans.size() >= 2);
    }

    @Test
    void testUpdateFan() {
        fanService.addFan("F005", "Hoang Van E", "e@example.com", "0333333333");
        Fan fan = fanService.findById("F005");
        fan.setEmail("new_e@example.com");
        fanService.updateFan(fan);

        Fan updated = fanService.findById("F005");
        assertEquals("new_e@example.com", updated.getEmail());
    }
}
