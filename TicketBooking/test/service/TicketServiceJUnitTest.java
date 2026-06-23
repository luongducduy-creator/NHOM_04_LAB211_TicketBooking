package service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TicketServiceJUnitTest {

    private TicketService service;

    @Before
    public void setUp() {
        // ✅ truyền đường dẫn file CSV thay vì Scanner
        service = new TicketService("data/tickets.csv");
    }

    @Test
    public void testLoadTickets() {
        assertFalse("Danh sách vé rỗng!", service.getAllTickets().isEmpty());
    }

    @Test
    public void testTicketById() {
        assertNotNull("Không tìm thấy vé T001!", service.getTicketById("T001"));
    }
}
