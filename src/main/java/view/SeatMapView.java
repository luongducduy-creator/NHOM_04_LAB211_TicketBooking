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

        // Sorted row numbers
        List<Integer> sortedRows = seatMap.keySet().stream()
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());

        // Determine the maximum number of seats in any row (for header)
        final int COLUMNS_PER_ROW = 20;
        int maxColumns = seatMap.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);
        // Cap the displayed columns to the defined limit for better readability
        maxColumns = Math.min(maxColumns, COLUMNS_PER_ROW);
        // Fixed column width for each seat label (e.g., "[12]")
        int colWidth = 5; // "[X] " is 4 chars, add one for spacing

        // Print full column header (numbers) aligned under row label area
        System.out.print("║        ");
        for (int col = 1; col <= maxColumns; col++) {
            System.out.printf("%-" + colWidth + "s", col);
        }
        System.out.println("║");

        // Render each row
        for (Integer rowNum : sortedRows) {
            String rowKey = String.valueOf(rowNum);
            List<Seat> seatsInRow = seatMap.get(rowKey);
            seatsInRow.sort((s1, s2) -> Integer.compare(Integer.parseInt(s1.getNumber()), Integer.parseInt(s2.getNumber())));

            System.out.print("║  ROW " + padRight(rowKey + ":", 4) + " ");
            for (int colIdx = 0; colIdx < maxColumns; colIdx++) {
                if (colIdx < seatsInRow.size()) {
                    Seat seat = seatsInRow.get(colIdx);
                    boolean available = "AVAILABLE".equalsIgnoreCase(seat.getStatus());
                    String label = available ? "[" + seat.getNumber() + "]" : "[X]";
                    System.out.printf("%-" + colWidth + "s", label);
                } else {
                    System.out.printf("%-" + colWidth + "s", "");
                }
            }
            System.out.println("║");
        }

        printBorder('╚', '═', '╝', 50);
        System.out.println("  Legend:  [<num>] = Available   [X] = Booked/Unavailable");
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
