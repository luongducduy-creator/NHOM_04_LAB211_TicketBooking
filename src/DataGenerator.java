package repository;

import java.io.*;
import java.time.LocalDate;
import java.util.Random;

public class DataGenerator {
    static Random rand = new Random();

    public static void main(String[] args) throws IOException {
        int stadiumCount = 5; // số sân vận động
        int seatsPerStadium = 4000; // mỗi sân 4000 ghế
        int matchesPerStadium = 5; // mỗi sân có 5 trận
        int fansCount = 50000; // số fan
        int transactionsCount = 50000;

        generateStadiums(stadiumCount);
        generateSections(stadiumCount);
        generateSeats(stadiumCount, seatsPerStadium);
        generateFans(fansCount);
        generateMatches(stadiumCount, matchesPerStadium);
        generateTickets(stadiumCount, seatsPerStadium, matchesPerStadium);
        generateTransactions(transactionsCount, fansCount);
        System.out.println("✅ CSV files generated successfully in /data folder!");
    }

    static void generateStadiums(int count) throws IOException {
        try (PrintWriter pw = new PrintWriter("data/stadiums.csv")) {
            pw.println("stadiumId,name,location,capacity");
            for (int i = 1; i <= count; i++) {
                pw.printf("S%d,Stadium %d,City %d,4000%n", i, i, i);
            }
        }
    }

    static void generateSections(int stadiumCount) throws IOException {
        try (PrintWriter pw = new PrintWriter("data/sections.csv")) {
            pw.println("sectionId,stadiumId,name,type");
            int secId = 1;
            String[] names = { "A", "B", "C", "D" };
            for (int s = 1; s <= stadiumCount; s++) {
                int vipIndex = rand.nextInt(4); // chọn ngẫu nhiên 1 khu VIP
                for (int i = 0; i < 4; i++) {
                    String type = (i == vipIndex) ? "VIP" : "NORMAL";
                    pw.printf("SEC%d,S%d,Section %s,%s%n", secId++, s, names[i], type);
                }
            }
        }
    }

    static void generateSeats(int stadiumCount, int seatsPerStadium) throws IOException {
        // Ensure stadium capacity does not exceed 4000 seats
        if (seatsPerStadium > 4000) {
            System.out.println("[WARNING] seatsPerStadium capped at 4000 per stadium.");
            seatsPerStadium = 4000;
        }
        // Each stadium has 4 sections; enforce max 1000 seats per section
        int maxSeatsPerSection = Math.min(1000, seatsPerStadium / 4);
        try (PrintWriter pw = new PrintWriter("data/seats.csv")) {
            pw.println("seatId,sectionId,row,number,status,version");
            int seatId = 1;
            for (int s = 1; s <= stadiumCount; s++) {
                // Determine VIP section for this stadium (randomly one of four)
                int vipIndex = rand.nextInt(4); // 0..3
                int vipSectionId = (s - 1) * 4 + vipIndex + 1;
                // Generate seats for each of the 4 sections
                for (int sec = 1; sec <= 4; sec++) {
                    int sectionId = (s - 1) * 4 + sec;
                    int seatsForThisSection = maxSeatsPerSection;
                    // Distribute any remainder seats to sections sequentially (should be zero when seatsPerStadium=4000)
                    int remainder = seatsPerStadium - (maxSeatsPerSection * 4);
                    if (remainder > 0 && sec <= remainder) {
                        seatsForThisSection += 1;
                    }
                    for (int i = 1; i <= seatsForThisSection; i++) {
                        String type = (sectionId == vipSectionId) ? "VIP" : "NORMAL";
                        int columnsPerRow = 20;
                        int row = (i - 1) / columnsPerRow + 1;
                        int number = (i - 1) % columnsPerRow + 1;
                        pw.printf("SEAT%d,SEC%d,%d,%d,AVAILABLE,0%n", seatId++, sectionId, row, number);
                    }
                }
            }
        }
    }

    static void generateFans(int totalFans) throws IOException {
        // Skip generation if the fans.csv file already exists with data to preserve existing records
        java.io.File fanFile = new java.io.File("data/fans.csv");
        if (fanFile.exists() && fanFile.length() > 0) {
            // Existing data found; do not regenerate to avoid data loss
            return;
        }
        try (PrintWriter pw = new PrintWriter("data/fans.csv")) {
            pw.println("fanId,fullName,email,phone,birthYear,password");
            for (int i = 1; i <= totalFans; i++) {
                pw.printf("\"FAN%d\",\"Fan %d\",\"fan%d@gmail.com\",\"09%08d\",%d,\"password123\"%n",
                    i, i, i, rand.nextInt(99999999), 1980 + rand.nextInt(25));
            }
        }
    }

    static void generateMatches(int stadiumCount, int matchesPerStadium) throws IOException {
        try (PrintWriter pw = new PrintWriter("data/matches.csv")) {
            pw.println("matchId,homeTeam,awayTeam,date,stadiumId");
            int matchId = 1;
            for (int s = 1; s <= stadiumCount; s++) {
                for (int m = 1; m <= matchesPerStadium; m++) {
                    LocalDate date = LocalDate.of(2026, 7, m + s);
                    pw.printf("M%d,Team%d,Team%d,%s,S%d%n", matchId++, rand.nextInt(50) + 1, rand.nextInt(50) + 1, date,
                            s);
                }
            }
        }
    }

    static void generateTickets(int stadiumCount, int seatsPerStadium, int matchesPerStadium) throws IOException {
        try (PrintWriter pw = new PrintWriter("data/tickets.csv")) {
            pw.println("ticketId,matchId,seatId,type,price,date,status");
            int ticketId = 1;
            for (int s = 1; s <= stadiumCount; s++) {
                for (int m = 1; m <= matchesPerStadium; m++) {
                    LocalDate date = LocalDate.of(2026, 7, m + s);
                    for (int seat = 1; seat <= seatsPerStadium; seat++) {
                        String type = (seat <= seatsPerStadium * 0.1) ? "VIP" : "NORMAL";
                        int price = type.equals("VIP") ? 800000 : 300000;
                        String status = rand.nextBoolean() ? "Available" : "Sold";
                        pw.printf("T%d,M%d,SEAT%d,%s,%d,%s,%s%n", ticketId++, (s - 1) * matchesPerStadium + m,
                                ((s - 1) * seatsPerStadium) + seat, type, price, date, status);
                    }
                }
            }
        }
    }

    static void generateTransactions(int totalTrans, int totalFans) throws IOException {
        try (PrintWriter pw = new PrintWriter("data/transactions.csv")) {
            pw.println("transactionId,ticketId,fanId,amount,paymentMethod,status");
            for (int i = 1; i <= totalTrans; i++) {
                String method = (i % 2 == 0) ? "ONLINE" : "CASH";
                String status = method.equals("ONLINE") ? "CONFIRMED" : "SUCCESS";
                int amount = (method.equals("ONLINE")) ? 800000 : 300000;
                int fanId = rand.nextInt(totalFans) + 1;

                pw.printf("TR%d,T%d,FAN%d,%d,%s,%s%n", i, i, fanId, amount, method, status);

                // thêm refund cho 5% vé ONLINE
                if (method.equals("ONLINE") && rand.nextInt(100) < 5) {
                    pw.printf("TR%d,T%d,FAN%d,%d,%s,REFUNDED%n", totalTrans + i, i, fanId, amount, method, "REFUNDED");
                }
            }
        }
    }
}
