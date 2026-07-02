package service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class FanServiceJUnitTest {

    private FanService service;

    @Before
    public void setUp() {
        // ✅ truyền đường dẫn file CSV thay vì Scanner
        service = new FanService("data/fans.csv");
    }

    @Test
    public void testLoadFans() {
        assertFalse("Danh sách fan rỗng!", service.getAllFans().isEmpty());
    }

    @Test
    public void testFanById() {
        assertNotNull("Không tìm thấy fan F001!", service.getFanById("F001"));
    }
}
