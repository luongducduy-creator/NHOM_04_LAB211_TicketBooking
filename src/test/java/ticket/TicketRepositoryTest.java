package ticket;

import repository.TicketRepository;
import model.ticket.Ticket;
import model.ticket.TicketStatus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TicketRepositoryTest {

    // Đường dẫn file CSV — chỉnh lại nếu file nằm ở nơi khác
    private final TicketRepository repo = new TicketRepository("data/tickets.csv");

    @Test
    public void testCountByStatus() {
        System.out.println("=== TEST COUNT BY STATUS ===");

        long sold = repo.countByStatus(TicketStatus.SOLD);
        long available = repo.countByStatus(TicketStatus.AVAILABLE);

        System.out.println("SOLD = " + sold);
        System.out.println("AVAILABLE = " + available);

        // Nếu không đọc được file, cả hai sẽ bằng 0
        if (sold + available == 0) {
            System.out.println("⚠️ Không đọc được dữ liệu từ file CSV! Kiểm tra lại đường dẫn hoặc nội dung file.");
        }

        assertTrue(sold + available > 0, "Tổng số vé phải lớn hơn 0");
    }

    @Test
    public void testFindByIdDetails() {
        System.out.println("=== TEST FIND BY ID ===");

        Ticket ticket = repo.findById("T20033");

        assertNotNull(ticket, "Ticket không tồn tại!");

        System.out.println("ID: " + ticket.getTicketId());
        System.out.println("Match: " + ticket.getMatchId());
        System.out.println("Seat: " + ticket.getSeatId());
        System.out.println("SeatType: " + ticket.getSeatType());
        System.out.println("Price: " + ticket.getPrice());
        System.out.println("Date: " + ticket.getDate());
        System.out.println("Status: " + ticket.getStatus());
    }

}
