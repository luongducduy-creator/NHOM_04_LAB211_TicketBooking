package view;

import model.seat.Seat;
import model.seat.Section;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * T6 – SeatMapView
 * Renders an ASCII seat map in the console.
 */
public class SeatMapView {

    /**
     * Render the complete seat map for a section.
     *
     * @param seatMap result of StadiumController.buildSeatMap(sectionId)
     * @param section the Section object (for header info)
     */
    public void display(Map<String, List<Seat>> seatMap, Section section, List<String> bookedSeatIds) {
        if (seatMap == null || seatMap.isEmpty()) {
            System.out.println("  No seats found for this section.");
            return;
        }

        String sectionHeader = section != null
                ? section.getSectionId() + " - " + section.getName() + " (" + section.getType() + ")"
                : "Section";

        // Count stats
        int totalSeats = 0, available = 0, booked = 0;
        for (List<Seat> row : seatMap.values()) {
            totalSeats += row.size();
            for (Seat s : row) {
                boolean isAvail = "AVAILABLE".equalsIgnoreCase(s.getStatus()) && (bookedSeatIds == null || !bookedSeatIds.contains(s.getSeatId()));
                if (isAvail) available++;
                else booked++;
            }
        }

        System.out.println("\n  ╔══════════════════════════════════════════════════════╗");
        System.out.printf ("  ║  SEAT MAP: %-42s║%n", sectionHeader);
        System.out.printf ("  ║  Total: %-5d  Available: %-5d  Booked: %-10d║%n", totalSeats, available, booked);
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println("  Legend: [ ] = Available   [X] = Booked");
        System.out.println();

        // Sorted rows
        List<Integer> sortedRows = seatMap.keySet().stream()
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());

        final int SEATS_PER_LINE = 20;

        for (Integer rowNum : sortedRows) {
            String rowKey = String.valueOf(rowNum);
            List<Seat> seatsInRow = seatMap.get(rowKey);
            seatsInRow.sort((s1, s2) ->
                    Integer.compare(Integer.parseInt(s1.getNumber()), Integer.parseInt(s2.getNumber())));

            System.out.printf("  ROW %2d │ ", rowNum);
            for (int i = 0; i < seatsInRow.size(); i++) {
                Seat s = seatsInRow.get(i);
                boolean avail = "AVAILABLE".equalsIgnoreCase(s.getStatus()) && (bookedSeatIds == null || !bookedSeatIds.contains(s.getSeatId()));
                if (avail) {
                    System.out.printf("[%s] ", s.getSeatId());
                } else {
                    System.out.print("[X] ");
                }
                // Line break every SEATS_PER_LINE seats (except last)
                if ((i + 1) % SEATS_PER_LINE == 0 && i + 1 < seatsInRow.size()) {
                    System.out.printf("%n         │ ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Print only available seats list.
     */
    public void displayAvailableSeats(List<Seat> availableSeats, String sectionId) {
        System.out.println("  ╔══════════════════════════════════════════════════════════════════════╗");
        printCentered("AVAILABLE SEATS - SECTION " + sectionId, 70);
        System.out.println("  ╠══════════════════════════════════════════════════════════════════════╣");
        
        if (availableSeats.isEmpty()) {
            System.out.println("  ║ No available seats.                                                  ║");
        } else {
            int count = 0;
            System.out.print("  ║ ");
            for (Seat s : availableSeats) {
                System.out.printf("[%s] ", s.getSeatId());
                count++;
                if (count % 6 == 0) {
                    System.out.println(" ║");
                    if (count < availableSeats.size()) {
                        System.out.print("  ║ ");
                    }
                }
            }
            if (count % 6 != 0) {
                System.out.println(); // Just a simple newline, padding the right border exactly is tricky with variable seat length
            }
        }
        System.out.println("  ╠══════════════════════════════════════════════════════════════════════╣");
        System.out.printf ("  ║ Total Available Seats: %-46d║%n", availableSeats.size());
        System.out.println("  ╚══════════════════════════════════════════════════════════════════════╝");
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
