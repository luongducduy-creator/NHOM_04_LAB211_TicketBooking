package app;

import controller.SimulatorController;
import controller.SimulatorController.BookingResult;
import controller.SimulatorController.SyncMechanism;
import controller.StadiumController;
import controller.BookingController;
import model.seat.Seat;
import model.ticket.Ticket;
import model.ticket.TicketStatus;
import view.SimulatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Demo: mỗi cơ chế cho NHIỀU FAN cùng tranh MỘT ghế duy nhất
 * → chỉ 1 người thắng, còn lại thất bại → thấy rõ sự khác biệt lock.
 */
public class SimulatorDemo {

    private static final int FANS_PER_SEAT = 3; // số fan cùng tranh 1 ghế
    private static final int MECHANISMS    = 3; // SYNCHRONIZED, FILE_LOCK, OPTIMISTIC
    private static final int SEATS_NEEDED  = MECHANISMS; // 1 ghế per cơ chế

    public static void main(String[] args) {

        StadiumController stadiumCtrl = new StadiumController();
        BookingController bookingCtrl = new BookingController(stadiumCtrl);
        SimulatorController simulator = new SimulatorController(bookingCtrl);
        SimulatorView view = new SimulatorView();

        String matchId   = "M1";
        String sectionId = "SEC1";

        // Lấy ghế chưa có ticket SOLD nào trong match này
        Set<String> alreadySold = new HashSet<>();
        for (Ticket t : bookingCtrl.getTicketRepo().findAll()) {
            if (t.getMatchId().equals(matchId) && t.getStatus() == TicketStatus.SOLD) {
                alreadySold.add(t.getSeatId().toUpperCase());
            }
        }

        List<Seat> candidates = stadiumCtrl.getAvailableSeats(sectionId);
        List<Seat> freshSeats = new ArrayList<>();
        for (Seat s : candidates) {
            if (!alreadySold.contains(s.getSeatId().toUpperCase())) {
                freshSeats.add(s);
                if (freshSeats.size() == SEATS_NEEDED) break;
            }
        }

        if (freshSeats.size() < SEATS_NEEDED) {
            System.out.println("[ERROR] Không đủ ghế trống. Cần " + SEATS_NEEDED
                    + ", tìm được " + freshSeats.size());
            return;
        }

        // Fan groups – mỗi nhóm tranh CÙNG 1 ghế
        String[][] fanGroups = {
            { "FAN10", "FAN11", "FAN12" }, // SYNCHRONIZED  → tranh ghế [0]
            { "FAN13", "FAN14", "FAN15" }, // FILE_LOCK      → tranh ghế [1]
            { "FAN16", "FAN17", "FAN18" }, // OPTIMISTIC     → tranh ghế [2]
        };

        SyncMechanism[] mechanisms = {
            SyncMechanism.SYNCHRONIZED,
            SyncMechanism.FILE_LOCK,
            SyncMechanism.OPTIMISTIC
        };

        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║  CONCURRENT BOOKING — MULTIPLE FANS COMPETE FOR ONE SEAT  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Scenario: " + FANS_PER_SEAT + " fans book the same seat — only 1 wins.\n");

        for (int i = 0; i < MECHANISMS; i++) {
            SyncMechanism mechanism = mechanisms[i];
            String contestedSeat = freshSeats.get(i).getSeatId();
            List<String> fanIds  = List.of(fanGroups[i]);

            // All fans in the group compete for the SAME seat
            List<String> seatIds = Collections.nCopies(FANS_PER_SEAT, contestedSeat);

            System.out.println("Mechanism   : " + mechanism);
            System.out.println("Fans        : " + fanIds);
            System.out.println("Contested   : " + contestedSeat
                    + "  (" + FANS_PER_SEAT + " fans racing for this seat)");

            long startMs = System.currentTimeMillis();
            List<BookingResult> results = simulator.runSimulation(
                    fanIds, matchId, seatIds, FANS_PER_SEAT, mechanism);
            long elapsed = System.currentTimeMillis() - startMs;

            view.displayDetails(results);
            view.displaySummary(mechanism.name(), FANS_PER_SEAT, elapsed, results);
        }
    }
}
