package utils;

import java.io.*;
import java.util.*;

/**
 * Utility to synchronize seat statuses in seats.csv based on tickets.csv.
 * Seats that appear as SOLD in tickets.csv will have their status updated to SOLD in seats.csv.
 */
public class SeatStatusUpdater {
    /**
     * Updates the status column of seats.csv according to tickets.csv.
     *
     * @param seatsCsvPath   path to seats.csv (e.g., "data/seats.csv")
     * @param ticketsCsvPath path to tickets.csv (e.g., "data/tickets.csv")
     */
    public static void updateSeatStatus(String seatsCsvPath, String ticketsCsvPath) throws IOException {
        // 1. Load all seat IDs that are marked as SOLD in tickets.csv
        Set<String> soldSeatIds = new HashSet<>();
        try (BufferedReader ticketReader = new BufferedReader(new FileReader(ticketsCsvPath))) {
            String line;
            while ((line = ticketReader.readLine()) != null) {
                // Expected format: ticketId,memberId,seatId,category,price,date,status
                String[] parts = line.split(",");
                if (parts.length < 6) continue; // skip malformed lines
                String seatId = parts[2].trim();
                String status = parts[5].trim();
                if ("SOLD".equalsIgnoreCase(status)) {
                    soldSeatIds.add(seatId);
                }
            }
        }

        // 2. Read seats.csv, replace the status column where needed, and collect the new lines
        List<String> updatedLines = new ArrayList<>();
        try (BufferedReader seatReader = new BufferedReader(new FileReader(seatsCsvPath))) {
            String line = seatReader.readLine(); // header
            if (line != null) {
                updatedLines.add(line);
            }
            while ((line = seatReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String seatId = parts[0].trim();
                    if (soldSeatIds.contains(seatId)) {
                        // Change status to SOLD
                        parts[4] = "SOLD";
                        line = String.join(",", parts);
                    }
                }
                updatedLines.add(line);
            }
        }

        // 3. Overwrite seats.csv with the updated content
        try (BufferedWriter seatWriter = new BufferedWriter(new FileWriter(seatsCsvPath))) {
            for (String updatedLine : updatedLines) {
                seatWriter.write(updatedLine);
                seatWriter.newLine();
            }
        }
    }

    // Simple demo main method – can be invoked via "mvn exec:java -Dexec.mainClass=utils.SeatStatusUpdater"
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java utils.SeatStatusUpdater <seats.csv path> <tickets.csv path>");
            System.exit(1);
        }
        updateSeatStatus(args[0], args[1]);
        System.out.println("seats.csv has been synchronized with tickets.csv.");
    }
}
