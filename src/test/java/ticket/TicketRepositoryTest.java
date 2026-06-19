package ticket;

import repository.TicketRepository;
import model.ticket.Ticket;
import model.ticket.TicketStatus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TicketRepositoryTest {

    // Khởi tạo repository từ file CSV
    private final TicketRepository repo = new TicketRepository("data/tickets.csv");

    @Test
    public void testCountByStatus() {
        System.out.println("=== TEST COUNT BY STATUS ===");

        long sold = repo.countByStatus(TicketStatus.SOLD);
        long available = repo.countByStatus(TicketStatus.AVAILABLE);

        System.out.println("SOLD = " + sold);
        System.out.println("AVAILABLE = " + available);

        // Kiểm tra tổng số vé phải > 0
        assertTrue(sold + available > 0, "Tổng số vé phải lớn hơn 0");
    }

    @Test
    public void testFindById() {
        System.out.println("=== TEST FIND BY ID ===");

        Ticket ticket = repo.findById("T453");

        assertNotNull(ticket, "Ticket không tồn tại!");

        // In chi tiết vé ra màn hình
        repo.printTicketInfo(ticket);
    }

    @Test
    public void testSearchDynamicMatchId() {
        System.out.println("=== TEST SEARCH DYNAMIC MATCH ID ===");

        // 🧠 Bạn tự chọn giá trị muốn tìm
        String matchToSearch = "M11"; // đổi tùy thích khi test

        List<Ticket> result = repo.searchTickets(null, matchToSearch, null, null, null, null);

        if (result.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy vé nào cho trận " + matchToSearch);
        } else {
            System.out.println("✅ Tìm thấy " + result.size() + " vé cho trận " + matchToSearch);
            for (Ticket t : result) {
                System.out.println("Ticket ID: " + t.getTicketId() + " | Match: " + t.getMatchId());
                // Kiểm tra động: chỉ cần matchId trùng với giá trị bạn chọn
                assertEquals(matchToSearch, t.getMatchId());
            }
        }
    }

    @Test
    public void testSearchByTypePriceStatus() {
        System.out.println("=== TEST SEARCH BY TYPE + PRICE + STATUS ===");

        // 🧠 Bạn tự chọn giá trị muốn test
        String typeToSearch = "NORMAL"; // loại ghế
        double maxPrice = 300000; // giá tối đa
        TicketStatus statusToSearch = TicketStatus.AVAILABLE; // trạng thái vé

        // Gọi hàm tìm kiếm
        List<Ticket> result = repo.searchTickets(null, null, typeToSearch, maxPrice, null, statusToSearch);

        // In kết quả
        if (result.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy vé nào với Type=" + typeToSearch
                    + ", Price <= " + maxPrice + ", Status=" + statusToSearch);
        } else {
            System.out.println("✅ Tìm thấy " + result.size() + " vé phù hợp");
            for (Ticket t : result) {
                System.out.println("Ticket ID: " + t.getTicketId()
                        + " | Match: " + t.getMatchId()
                        + " | Type: " + t.getSeatType()
                        + " | Price: " + t.getPrice()
                        + " | Status: " + t.getStatus());

                // Kiểm tra logic
                assertEquals(typeToSearch, t.getSeatType());
                assertTrue(t.getPrice() <= maxPrice);
                assertEquals(statusToSearch, t.getStatus());
            }
        }
    }

    @Test
    public void testSearchCombinedConditions() {
        System.out.println("=== TEST SEARCH COMBINED CONDITIONS ===");

        // 🧠 Bạn tự chọn giá trị muốn test
        String matchIdToSearch = "M7"; // mã trận
        String typeToSearch = "NORMAL"; // loại ghế
        double maxPrice = 300000; // giá tối đa
        String dateToSearch = "2026-07-04"; // ngày diễn ra
        TicketStatus statusToSearch = TicketStatus.SOLD; // trạng thái vé

        // Gọi hàm tìm kiếm
        List<Ticket> result = repo.searchTickets(null, matchIdToSearch, typeToSearch, maxPrice, dateToSearch,
                statusToSearch);

        // In kết quả
        if (result.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy vé thỏa mãn tất cả điều kiện!");
        } else {
            System.out.println("✅ Tìm thấy " + result.size() + " vé phù hợp");
            for (Ticket t : result) {
                System.out.println("Ticket ID: " + t.getTicketId()
                        + " | Match: " + t.getMatchId()
                        + " | Type: " + t.getSeatType()
                        + " | Price: " + t.getPrice()
                        + " | Date: " + t.getDate()
                        + " | Status: " + t.getStatus());

                // Kiểm tra logic
                assertEquals(matchIdToSearch, t.getMatchId());
                assertEquals(typeToSearch, t.getSeatType());
                assertTrue(t.getPrice() <= maxPrice);
                assertEquals(dateToSearch, t.getDate());
                assertEquals(statusToSearch, t.getStatus());
            }
        }
    }

    // 🧩 Test thêm vé mới
    @Test
    public void testAddTicket() {
        System.out.println("=== TEST ADD TICKET ===");

        // Tạo vé mới
        Ticket newTicket = new Ticket(
                "T9999", "M20", "S100", "VIP",
                500000, "2026-07-10", TicketStatus.AVAILABLE);

        // Thêm vé vào repository
        repo.addTicket(newTicket);

        // Kiểm tra xem vé có tồn tại sau khi thêm
        Ticket found = repo.findById("T9999");
        assertNotNull(found, "Vé mới phải được thêm vào repository");
        assertEquals("VIP", found.getSeatType());
        assertEquals(500000, found.getPrice());
        assertEquals(TicketStatus.AVAILABLE, found.getStatus());

        System.out.println("✅ Vé mới đã được thêm thành công!");
        repo.printTicketInfo(found);
    }

    // 🧩 Test xóa vé
    @Test
    public void testRemoveTicket() {
        System.out.println("=== TEST REMOVE TICKET ===");

        String ticketIdToRemove = "T9999"; // ID vé vừa thêm ở test trên

        boolean removed = repo.removeTicket(ticketIdToRemove);

        assertTrue(removed, "Vé phải được xóa thành công");

        Ticket found = repo.findById(ticketIdToRemove);
        assertNull(found, "Sau khi xóa, vé không được tồn tại trong repository");

        System.out.println("✅ Vé " + ticketIdToRemove + " đã được xóa thành công!");
    }
}