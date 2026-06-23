package service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MatchServiceJUnitTest {

    private MatchService service;

    @Before
    public void setUp() {
        // ✅ truyền đường dẫn file CSV thay vì Scanner
        service = new MatchService("data/matches.csv");
    }

    @Test
    public void testLoadMatches() {
        assertFalse("Danh sách trận đấu rỗng!", service.getAllMatches().isEmpty());
    }

    @Test
    public void testMatchById() {
        assertNotNull("Không tìm thấy trận đấu M001!", service.getMatchById("M001"));
    }
}
