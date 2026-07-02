package service;

import model.Fan;
import repository.FanRepository;
import java.util.List;

public class FanService {
    private FanRepository fanRepo;

    public FanService(String filePath) {
        this.fanRepo = new FanRepository(filePath);
    }

    // Thêm fan mới
    public Fan addFan(String id, String name, String email, String phone) {
        Fan f = new Fan(id, name, email, phone);
        List<Fan> all = fanRepo.findAll();
        all.add(f);
        fanRepo.saveAll(all);
        return f;
    }

    // Tìm fan theo email
    public Fan findFanByEmail(String email) {
        return fanRepo.findAll()
                      .stream()
                      .filter(f -> f.getEmail().equals(email))
                      .findFirst()
                      .orElse(null);
    }

    // Tìm fan theo số điện thoại
    public Fan findFanByPhone(String phone) {
        return fanRepo.findAll()
                      .stream()
                      .filter(f -> f.getPhone().equals(phone))
                      .findFirst()
                      .orElse(null);
    }

    // Lấy toàn bộ fan
    public List<Fan> getAllFans() {
        return fanRepo.findAll();
    }

    // Xóa fan theo ID
    public void deleteFan(String fanId) {
        List<Fan> all = fanRepo.findAll();
        all.removeIf(f -> f.getId().equals(fanId));
        fanRepo.saveAll(all);
    }

    // ✅ Thêm method để phục vụ JUnit Test
    public Fan getFanById(String id) {
        return fanRepo.findAll()
                      .stream()
                      .filter(f -> f.getId().equals(id))
                      .findFirst()
                      .orElse(null);
    }
}
