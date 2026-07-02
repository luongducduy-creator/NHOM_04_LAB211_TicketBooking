package repository;

import model.Seat;
import java.util.List;

public class SeatRepository extends CsvRepository<Seat> {
    public SeatRepository(String filePath) {
        super(filePath, Seat::new);
    }

    // Ví dụ: tìm seat theo code
    public List<Seat> findByCode(String code) {
        return findByCondition(s -> s.getCode().equals(code));
    }
}
