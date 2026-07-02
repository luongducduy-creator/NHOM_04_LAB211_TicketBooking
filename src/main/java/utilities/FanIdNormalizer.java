package utilities;

import java.io.*;
import java.util.*;

/**
 * Utility to rewrite fan IDs in fans.csv to a consistent "FAN<number>" format.
 * It reads the existing file, assigns sequential IDs (starting at 1), and writes back.
 * All other fields (including password if present) are preserved.
 */
public class FanIdNormalizer {
    public static void main(String[] args) throws IOException {
        String base = System.getProperty("user.dir");
        String inputPath = base + "/data/fans.csv";
        String tempPath = base + "/data/fans_normalized.tmp";
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempPath))) {
            String line;
            int counter = 1;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip empty lines
                String[] parts = line.split(",", -1); // keep empty fields
                // Ensure we have at least 5 fields (id, name, email, phone, birthYear) and possibly password
                if (parts.length < 5) {
                    // Write line unchanged if it doesn't look like a data row
                    bw.write(line);
                    bw.newLine();
                    continue;
                }
                // Replace id with FAN + counter
                parts[0] = "FAN" + counter;
                // Rebuild line
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                    sb.append(parts[i]);
                    if (i < parts.length - 1) sb.append(',');
                }
                bw.write(sb.toString());
                bw.newLine();
                counter++;
            }
        }
        // Replace original file with normalized version
        new File(tempPath).renameTo(new File(inputPath));
        System.out.println("[OK] fans.csv IDs normalized to FAN<number> format (" + (new File(inputPath).length()) + " bytes).");
    }
}
