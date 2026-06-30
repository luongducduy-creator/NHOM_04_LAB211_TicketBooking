package utilities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvSplitter reads the existing fans.csv file and creates three separate CSV files:
 *   - fans.csv   : records that are NOT staff or admin
 *   - admin.csv  : records whose email contains "admin"
 *   - staff.csv  : records whose email contains "staff"
 *
 * This utility is intended to be run after the project is built:
 *   1. mvn clean install
 *   2. java -cp target/classes utilities.CsvSplitter
 */
public class CsvSplitter {
    private static final String INPUT_FILE = System.getProperty("user.dir") + "/data/fans.csv";
    private static final String FAN_OUTPUT   = System.getProperty("user.dir") + "/data/fans_only.csv";
    private static final String ADMIN_OUTPUT = System.getProperty("user.dir") + "/data/admin.csv";
    private static final String STAFF_OUTPUT = System.getProperty("user.dir") + "/data/staff.csv";

    public static void main(String[] args) {
        List<String> fanLines   = new ArrayList<>();
        List<String> adminLines = new ArrayList<>();
        List<String> staffLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Determine role by simple email heuristics
                String lower = line.toLowerCase();
                if (lower.contains("admin")) {
                    adminLines.add(line);
                } else if (lower.contains("staff")) {
                    staffLines.add(line);
                } else {
                    fanLines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            return;
        }

        // Write the three output files (overwrite if they already exist)
        writeLines(FAN_OUTPUT, fanLines);
        writeLines(ADMIN_OUTPUT, adminLines);
        writeLines(STAFF_OUTPUT, staffLines);
        System.out.println("Split completed: ");
        System.out.println("Fans  : " + fanLines.size());
        System.out.println("Admin : " + adminLines.size());
        System.out.println("Staff : " + staffLines.size());
    }

    private static void writeLines(String path, List<String> lines) {
        try {
            // Ensure parent directory exists
            Files.createDirectories(Path.of(path).getParent());
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + path + ": " + e.getMessage());
        }
    }
}
