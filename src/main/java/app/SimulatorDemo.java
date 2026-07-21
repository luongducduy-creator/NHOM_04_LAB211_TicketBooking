package app;

import controller.SimulatorController;
import controller.SimulatorController.SyncMechanism;
import controller.StadiumController;
import controller.BookingController;
import model.seat.Seat;
import view.SimulatorView;

import java.util.List;

/**
 * Demo class to run the concurrent seat‑booking simulation for ALL three
 * synchronization mechanisms and display comparison results.
 * This class is ONLY for demonstration / testing –
 * it does NOT alter any existing application logic.
 */
public class SimulatorDemo {

    public static void main(String[] args) {

        // 1. Tạo các controller thực
        StadiumController stadiumCtrl = new StadiumController();
        BookingController bookingCtrl = new BookingController(stadiumCtrl);
        SimulatorController simulator  = new SimulatorController(bookingCtrl);
        SimulatorView view = new SimulatorView();

        String matchId   = "M1";    // phải tồn tại trong matches.csv
        String sectionId = "SEC1";  // section để lấy ghế AVAILABLE
        int threadsPerRun    = 3;   // số fan mỗi cơ chế
        int totalMechanisms  = 3;   // SYNCHRONIZED, FILE_LOCK, OPTIMISTIC
        int seatsNeeded = threadsPerRun * totalMechanisms; // 9 ghế

        // 2. Tự động lấy ghế AVAILABLE – không hardcode, luôn hoạt động
        List<Seat> availableSeats = stadiumCtrl.getAvailableSeats(sectionId);
        if (availableSeats.size() < seatsNeeded) {
            System.out.println("[ERROR] Không đủ ghế AVAILABLE trong " + sectionId
                    + ". Cần " + seatsNeeded + " ghế, hiện có " + availableSeats.size() + ".");
            return;
        }

        // 3. Fan IDs tồn tại trong fans.csv
        String[][] fanGroups = {
            { "FAN10", "FAN11", "FAN12" }, // SYNCHRONIZED
            { "FAN13", "FAN14", "FAN15" }, // FILE_LOCK
            { "FAN16", "FAN17", "FAN18" }, // OPTIMISTIC
        };

        SyncMechanism[] mechanisms = {
            SyncMechanism.SYNCHRONIZED,
            SyncMechanism.FILE_LOCK,
            SyncMechanism.OPTIMISTIC
        };

        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       CONCURRENT SEAT-BOOKING SIMULATION — ALL MODES     ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        for (int i = 0; i < mechanisms.length; i++) {
            SyncMechanism mechanism = mechanisms[i];
            List<String> fanIds = List.of(fanGroups[i]);

            // Mỗi cơ chế dùng 3 ghế tiếp theo từ danh sách AVAILABLE
            int offset = i * threadsPerRun;
            List<String> seatIds = List.of(
                availableSeats.get(offset).getSeatId(),
                availableSeats.get(offset + 1).getSeatId(),
                availableSeats.get(offset + 2).getSeatId()
            );

            System.out.println("\n▶ Mechanism : " + mechanism);
            System.out.println("  Fans  : " + fanIds);
            System.out.println("  Seats : " + seatIds);

            long startMs = System.currentTimeMillis();
            var results  = simulator.runSimulation(fanIds, matchId, seatIds, threadsPerRun, mechanism);
            long elapsed = System.currentTimeMillis() - startMs;

            view.displayDetails(results);
            view.displaySummary(mechanism.name(), threadsPerRun, elapsed, results);
        }
    }
}
