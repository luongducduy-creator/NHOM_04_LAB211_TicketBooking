package utilities;

import java.io.*;
import java.util.*;

/**
 * Utility: regenerate seats.csv with exactly 1000 seats per section.
 * Layout: 10 rows x 100 seats per section, clean and ordered.
 */
public class SeatDataGenerator {

    public static void main(String[] args) throws IOException {
        String base = System.getProperty("user.dir");
        String sectionsFile = base + "/data/sections.csv";
        String seatsFile    = base + "/data/seats.csv";

        // 1. Read all sectionIds from sections.csv
        List<String> sectionIds = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(sectionsFile))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                String[] p = line.split(",");
                if (p.length >= 1 && !p[0].isBlank()) {
                    sectionIds.add(p[0].trim());
                }
            }
        }

        System.out.println("Sections found: " + sectionIds);

        // 2. Generate seats: 10 rows x 100 seats = 1000 per section
        int ROWS_PER_SECTION = 50;
        int SEATS_PER_ROW    = 20;
        int seatCounter = 1;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(seatsFile))) {
            bw.write("seatId,sectionId,row,number,status");
            bw.newLine();
            for (String sectionId : sectionIds) {
                for (int row = 1; row <= ROWS_PER_SECTION; row++) {
                    for (int num = 1; num <= SEATS_PER_ROW; num++) {
                        bw.write("SEAT" + seatCounter + "," + sectionId + "," + row + "," + num + ",AVAILABLE");
                        bw.newLine();
                        seatCounter++;
                    }
                }
            }
        }

        System.out.println("Done! Generated " + (seatCounter - 1) + " seats across " + sectionIds.size() + " sections.");
        System.out.println("File saved: " + seatsFile);
    }
}
