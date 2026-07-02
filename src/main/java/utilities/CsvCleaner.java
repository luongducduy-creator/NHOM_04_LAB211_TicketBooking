package utilities;

import java.io.*;

public class CsvCleaner {
    public static void main(String[] args) throws IOException {
        String base = System.getProperty("user.dir");
        String inputPath = base + "/data/fans.csv";
        String tempPath = base + "/data/fans_cleaned.tmp";
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove surrounding double quotes from each field
                String cleaned = line.replaceAll("\"", "");
                bw.write(cleaned);
                bw.newLine();
            }
        }
        // Replace original file with cleaned version
        new File(tempPath).renameTo(new File(inputPath));
        System.out.println("[OK] fans.csv cleaned (quotes removed).");
    }
}
