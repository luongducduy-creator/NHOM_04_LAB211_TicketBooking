package ticket;

import model.ticket.Ticket;
import model.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TicketCsvTest {

    @Test
    public void testTicketToStringFormat() {
        Ticket ticket = new Ticket("T20033", "M1001", "S45", "VIP",
                150.0, "2026-06-19", TicketStatus.SOLD);

        String csv = ticket.toString();
        System.out.println("CSV format: " + csv);

        // Kiểm tra số lượng cột
        String[] parts = csv.split(",");
        assertEquals(7, parts.length, "CSV phải có đúng 7 cột");

        // Kiểm tra dữ liệu không rỗng
        assertFalse(parts[0].isBlank(), "ticketId không được rỗng");
        assertFalse(parts[1].isBlank(), "matchId không được rỗng");
        assertFalse(parts[2].isBlank(), "seatId không được rỗng");
        assertFalse(parts[3].isBlank(), "seatType không được rỗng");
        assertFalse(parts[5].isBlank(), "date không được rỗng");

        // Kiểm tra price là số
        assertDoesNotThrow(() -> Double.parseDouble(parts[4]), "price phải là số");

        // Kiểm tra status hợp lệ
        assertTrue(parts[6].equals("SOLD") || parts[6].equals("AVAILABLE") || parts[6].equals("CANCELLED"),
                "status phải là SOLD, AVAILABLE hoặc CANCELLED");
    }

    @Test
    public void testRoundTripCsvParse() {
        Ticket original = new Ticket("T20034", "M1002", "S46", "STANDARD",
                100.0, "2026-06-20", TicketStatus.AVAILABLE);

        String csv = original.toString();
        Ticket parsed = Ticket.fromCsv(csv);

        assertNotNull(parsed, "Parse từ CSV phải ra Ticket hợp lệ");
        assertEquals(original, parsed, "Ticket parse lại phải bằng Ticket gốc");
    }
}
