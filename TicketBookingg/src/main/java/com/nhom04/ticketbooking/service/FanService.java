package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.fan.Fan;
import com.nhom04.ticketbooking.repository.FanRepository;

import java.util.List;

public class FanService {
    private final FanRepository fanRepository;

    // Constructor nhận filePath
    public FanService(String filePath) {
        this.fanRepository = new FanRepository(filePath);
    }

    // Thêm fan mới (4 tham số: id, name, email, phone)
    public void addFan(String id, String name, String email, String phone) {
        Fan fan = new Fan(id, name, email, phone);
        fanRepository.save(fan);
    }

    // Tìm fan theo ID
    public Fan findById(String id) {
        return fanRepository.getById(id);
    }

    // Lấy tất cả fan
    public List<Fan> findAll() {
        return fanRepository.getAll();
    }

    // Cập nhật fan
    public void updateFan(Fan fan) {
        fanRepository.save(fan);
    }
}
