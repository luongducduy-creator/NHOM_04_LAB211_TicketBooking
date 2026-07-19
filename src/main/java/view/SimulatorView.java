package view;

import controller.SimulatorController.BookingResult;
import java.util.List;

/**
 * Renders the result of a {@link controller.SimulatorController} run as an ASCII table.
 * The view prints directly to System.out.
 */
public class SimulatorView {

    /**
     * Displays a list of BookingResult objects in a formatted table.
     *
     * @param results the simulation outcomes
     */
    public void display(List<BookingResult> results) {
        if (results == null || results.isEmpty()) {
            System.out.println("[INFO] No simulation results to display.");
            return;
        }
        String header = String.format("%-12s | %-12s | %-10s | %-8s | %-15s | %s",
                "FanID", "MatchID", "SeatID", "Success", "TransactionID", "Error");
        System.out.println(header);
        System.out.println("-".repeat(header.length()));
        for (BookingResult r : results) {
            String row = String.format("%-12s | %-12s | %-10s | %-8s | %-15s | %s",
                    r.fanId,
                    r.matchId,
                    r.seatId,
                    r.success ? "YES" : "NO",
                    r.transactionId == null ? "-" : r.transactionId,
                    r.errorMessage == null ? "" : r.errorMessage);
            System.out.println(row);
        }
    }
}
