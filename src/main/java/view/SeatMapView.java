package view;

import model.seat.Seat;
import model.seat.Section;

import java.util.List;
import java.util.Map;

/**
 * T6 – SeatMapView
 * Renders an ASCII seat map in the console.
 *
 * Example output:
 * ╔══════════════════════════════════════════╗
 * ║         SECTION: SEC1 (VIP)             ║
 * ╠══════════════════════════════════════════╣
 * ║  ROW 1:  [A1] [A2] [XX] [A4] [A5]      ║
 * ║  ROW 2:  [B1] [B2] [B3] [XX] [B5]      ║
 * ╚══════════════════════════════════════════╝
 *  [ ] = Available   [X] = Booked
 */
public class SeatMapView {

    private static final int SEATS_PER_ROW_DISPLAY = 10; // wrap after N seats

    /**
     * Render the complete seat map for a section.
     *
     * @param seatMap  result of StadiumController.buildSeatMap(sectionId)
     * @param section  the Section object (for header info)
     */
    public void display(Map<String, List<Seat>> seatMap, Section section) {
        if (seatMap == null || seatMap.isEmpty()) {
            System.out.println("  No seats found for this section.");
            return;
        }

        String sectionHeader = section != null
                ? section.getSectionId() + " - " + section.getName() + " (" + section.getType() + ")"
                : "Section";

        printBorder('╔', '═', '╗', 50);
        printCentered("SEAT MAP: " + sectionHeader, 50);
        printBorder('╠', '═', '╣', 50);

        for (Map.Entry<String, List<Seat>> entry : seatMap.entrySet()) {
            String row = entry.getKey();
            List<Seat> seats = entry.getValue();

            System.out.print("║ ROW " + padRight(row + ":", 4) + " ");
            int count = 0;
            for (Seat seat : seats) {
                boolean available = "AVAILABLE".equalsIgnoreCase(seat.getStatus());
                String seatNum = seat.getSeatId().replaceAll("[^0-9]", "");
                String label = available ? "[" + seatNum + "]" : "[XX]";
                System.out.printf("%-6s", label); // fixed width for alignment
                count++;
                if (count % SEATS_PER_ROW_DISPLAY == 0 && count < seats.size()) {
                    System.out.println("║");
                    System.out.print("║              ");
                }
            }
            System.out.println("║");
        }

        printBorder('╚', '═', '╝', 50);
        System.out.println("  Legend:  [N] = Available   [XX] = Booked/Unavailable");
        System.out.println();
    }

    /**
     * Print only available seats list.
     */
    public void displayAvailableSeats(List<Seat> availableSeats, String sectionId) {
        System.out.println("\n  Available seats in section " + sectionId + ":");
        if (availableSeats.isEmpty()) {
            System.out.println("  No available seats.");
            return;
        }
        int count = 0;
        for (Seat s : availableSeats) {
            System.out.printf("  %-12s", s.getSeatId() + "(R" + s.getRow() + "N" + s.getNumber() + ")");
            if (++count % 5 == 0) System.out.println();
        }
        System.out.println();
    }

    // ─────── helpers ───────
    private void printBorder(char left, char fill, char right, int width) {
        System.out.print(left);
        for (int i = 0; i < width; i++) System.out.print(fill);
        System.out.println(right);
    }

    private void printCentered(String text, int width) {
        int padding = (width - text.length()) / 2;
        System.out.print("|");
        for (int i = 0; i < padding; i++) System.out.print(" ");
        System.out.print(text);
        for (int i = 0; i < width - padding - text.length(); i++) System.out.print(" ");
        System.out.println("|");
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
