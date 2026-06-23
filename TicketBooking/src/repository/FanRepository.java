package repository;

import com.nhom04.ticketbooking.model.fan.Fan;
import java.util.List;

public class FanRepository extends CsvRepository<Fan> {

    public FanRepository(String filePath) {
        super(filePath, Fan::new);
    }

    // Ví dụ: tìm fan theo email
    public List<Fan> findByEmail(String email) {
        return findByCondition(f -> f.getEmail().equals(email));
    }

    // Ví dụ: tìm fan theo số điện thoại
    public List<Fan> findByPhone(String phone) {
        return findByCondition(f -> f.getPhone().equals(phone));
    }
}
