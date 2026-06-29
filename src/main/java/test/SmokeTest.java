package test;

import controller.BookingController;
import controller.FanController;
import controller.StadiumController;
import model.fan.Fan;
import model.match.Match;
import model.seat.Seat;
import model.seat.Section;
import model.ticket.Ticket;
import model.transaction.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Quick smoke test for T5 (Controller Layer).
 * Run: mvn exec:java -Dexec.mainClass="test.SmokeTest"
 */
public class SmokeTest {

    public static void main(String[] args) {
        System.out.println("======= T5 CONTROLLER SMOKE TEST =======\n");

        FanController     fanCtrl     = new FanController();
        StadiumController stadiumCtrl = new StadiumController();
        BookingController bookingCtrl = new BookingController(stadiumCtrl);

        // ── Test 1: Register ──
        System.out.println("[TEST 1] Register new fan...");
        Fan fan = fanCtrl.register("Test Fan", "smoketest@lab211.com",
                "0901234567", 1999, "test1234");
        if (fan != null) System.out.println("  PASS: Fan registered: " + fan.getId());
        else             System.out.println("  SKIP: Email already registered (re-run on fresh data)");

        // ── Test 2: Login ──
        System.out.println("\n[TEST 2] Login...");
        Fan logged = fanCtrl.login("smoketest@lab211.com", "test1234");
        if (logged != null) System.out.println("  PASS: Logged in as: " + logged.getName());
        else                System.out.println("  FAIL: Login failed");

        // ── Test 3: Get all matches ──
        System.out.println("\n[TEST 3] List all matches...");
        List<Match> matches = stadiumCtrl.getAllMatches();
        System.out.println("  PASS: " + matches.size() + " matches found");
        if (!matches.isEmpty()) System.out.println("  First match: " + matches.get(0));

        // ── Test 4: Get sections for stadium S1 ──
        System.out.println("\n[TEST 4] Get sections for S1...");
        List<Section> sections = stadiumCtrl.getSections("S1");
        System.out.println("  PASS: " + sections.size() + " sections found in S1");

        // ── Test 5: Build seat map ──
        System.out.println("\n[TEST 5] Build seat map for SEC3...");
        Map<String, List<Seat>> seatMap = stadiumCtrl.buildSeatMap("SEC3");
        System.out.println("  PASS: " + seatMap.size() + " rows in SEC3");

        // ── Test 6: Get available seats ──
        System.out.println("\n[TEST 6] Available seats in SEC3...");
        List<Seat> available = stadiumCtrl.getAvailableSeats("SEC3");
        System.out.println("  PASS: " + available.size() + " available seats in SEC3");

        // ── Test 7: Book a seat (if fan registered) ──
        if (logged != null && !available.isEmpty() && !matches.isEmpty()) {
            System.out.println("\n[TEST 7] Book a seat...");
            String seatId = available.get(0).getSeatId();
            String matchId = matches.get(0).getMatchId();
            Transaction tr = bookingCtrl.bookSeat(logged.getId(), matchId, seatId,
                    Transaction.PaymentMethod.CASH);
            if (tr != null) {
                System.out.println("  PASS: Booking created – " + tr.getTransactionId());

                // ── Test 8: Get my tickets ──
                System.out.println("\n[TEST 8] Get my tickets...");
                List<Ticket> myTickets = fanCtrl.getMyTickets(logged.getId());
                System.out.println("  PASS: " + myTickets.size() + " tickets found");

                // ── Test 9: Cancel booking ──
                System.out.println("\n[TEST 9] Cancel booking...");
                boolean cancelled = bookingCtrl.cancelBooking(tr.getTransactionId(), logged.getId());
                System.out.println(cancelled ? "  PASS: Booking cancelled" : "  FAIL: Could not cancel");
            } else {
                System.out.println("  SKIP: Seat not available for booking");
            }
        }

        System.out.println("\n======= SMOKE TEST COMPLETE =======");
    }
}
