package repository;

import com.nhom04.ticketbooking.model.fan.Fan;
import java.util.List;

public class FanTest {
    public static void main(String[] args) {
        // Khởi tạo repository với file CSV fan
        FanRepository repo = new FanRepository("data/fans.csv");

        // CREATE: thêm fan mới
        Fan f1 = new Fan("F001", "Nguyen Van A", "a@gmail.com", "0123456789");
        repo.saveAll(List.of(f1));

        // READ: đọc toàn bộ file
        List<Fan> all = repo.findAll();
        System.out.println("Total fans: " + all.size());

        // UPDATE: sửa fan đầu tiên
        Fan f2 = all.get(0);
        f2.setEmail("updated@gmail.com");
        repo.saveAll(all);

        // DELETE: xóa fan đầu tiên
        all.remove(0);
        repo.saveAll(all);

        System.out.println("Fan CRUD test completed!");
    }
}
