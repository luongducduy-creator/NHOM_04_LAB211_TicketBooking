package repository;

import model.Seat;
import java.util.List;

public class SeatTest {
    public static void main(String[] args) {
        // Khởi tạo repository với file CSV seat
        SeatRepository repo = new SeatRepository("data/seats.csv");

        // CREATE: thêm seat mới
        Seat s1 = new Seat("S001", "A1", true);
        repo.saveAll(List.of(s1));

        // READ: đọc toàn bộ file
        List<Seat> all = repo.findAll();
        System.out.println("Total seats: " + all.size());

        // UPDATE: sửa seat đầu tiên
        Seat s2 = all.get(0);
        s2.setAvailable(false);
        repo.saveAll(all);

        // DELETE: xóa seat đầu tiên
        all.remove(0);
        repo.saveAll(all);

        System.out.println("Seat CRUD test completed!");
    }
}
