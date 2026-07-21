package view;

import controller.SimulatorController.BookingResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimulatorView {

    /**
     * Hàm display cũ (để giữ tương thích với SimulatorDemo.java không bị lỗi compile)
     */
    public void display(List<BookingResult> results) {
        displayDetails(results);
    }

    /**
     * In chi tiết từng kết quả đặt vé (Đã tối ưu chuẩn lề & màu ANSI)
     */
    public void displayDetails(List<BookingResult> results) {
        if (results == null || results.isEmpty()) {
            System.out.println("[INFO] No simulation results to display.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        // Header format
        String headerFormat = "| %-12s | %-12s | %-10s | %-6s | %-15s | %-20s |\n";
        String divider = "+--------------+--------------+------------+--------+-----------------+----------------------+\n";

        sb.append(divider);
        sb.append(String.format(headerFormat, "FanID", "MatchID", "SeatID", "Status", "TransactionID", "Error Message"));
        sb.append(divider);

        // Rows format
        for (BookingResult r : results) {
            String status = r.success ? "YES" : "NO";
            String rowStr = String.format(headerFormat,
                    r.fanId,
                    r.matchId,
                    r.seatId,
                    status,
                    r.transactionId == null ? "-" : r.transactionId,
                    r.errorMessage == null ? "" : r.errorMessage
            );

            // Bọc màu ANSI sau khi đã format lề chuẩn
            if (r.success) {
                rowStr = rowStr.replace("YES", "\u001B[32mYES\u001B[0m");
            } else {
                rowStr = rowStr.replace("NO ", "\u001B[31mNO \u001B[0m");
            }

            sb.append(rowStr);
        }
        sb.append(divider);

        System.out.print(sb.toString());
    }

    /**
     * BẢNG TỔNG HỢP (Dùng cho báo cáo & phân tích hiệu năng khi stress test)
     */
    public void displaySummary(String mechanism, int totalThreads, long totalTimeMs, List<BookingResult> results) {
        if (results == null) return;

        long successCount = results.stream().filter(r -> r.success).count();
        long failCount = results.size() - successCount;
        double throughput = totalTimeMs > 0 ? (results.size() * 1000.0 / totalTimeMs) : 0;

        StringBuilder sb = new StringBuilder();
        sb.append("\n=====================================================================\n");
        sb.append("                 SIMULATION SUMMARY REPORT                           \n");
        sb.append("=====================================================================\n");
        sb.append(String.format(" Locking Mechanism : %s\n", mechanism));
        sb.append(String.format(" Total Threads     : %d\n", totalThreads));
        sb.append(String.format(" Total Requests    : %d\n", results.size()));
        sb.append(String.format(" Successful        : \u001B[32m%d\u001B[0m\n", successCount));
        sb.append(String.format(" Failed            : \u001B[31m%d\u001B[0m\n", failCount));
        sb.append(String.format(" Execution Time    : %d ms\n", totalTimeMs));
        sb.append(String.format(" Throughput        : %.2f ops/sec\n", throughput));
        sb.append("=====================================================================\n\n");

        System.out.print(sb.toString());
    }

    /**
     * Simple Week 7 comparison table: several fans compete for the same seat.
     */
    public void displayWeek7Comparison(Map<String, List<BookingResult>> resultsByMechanism) {
        String line = "+---------------+----------+------------+----------+----------+-----------------------+";
        String format = "| %-13s | %8s | %10s | %8s | %8s | %-21s |%n";

        System.out.println();
        System.out.println("TINH HUONG: NHIEU FAN CUNG DAT MOT GHE SEAT1");
        System.out.println(line);
        System.out.printf(format, "CO CHE", "SO FAN", "THANH CONG", "THAT BAI", "TY LE", "KET LUAN");
        System.out.println(line);

        for (Map.Entry<String, List<BookingResult>> entry : resultsByMechanism.entrySet()) {
            List<BookingResult> results = entry.getValue();
            long success = results.stream().filter(BookingResult::success).count();
            long failed = results.size() - success;
            double successRate = results.isEmpty() ? 0.0 : success * 100.0 / results.size();
            String conclusion = success == 1 ? "KHONG TRUNG GHE" : "CO DOUBLE BOOKING";
            System.out.printf(format, entry.getKey(), results.size(), success, failed,
                    String.format("%.1f%%", successRate), conclusion);
        }
        System.out.println(line);
        System.out.println("Mong doi: moi co che chi co 1 fan dat ghe thanh cong.");
    }

    public static void main(String[] args) {
        List<BookingResult> demoResults = new ArrayList<>();

        demoResults.add(new BookingResult("Fan001", "MatchA", "Seat12", true, "TX12345", null));
        demoResults.add(new BookingResult("Fan002", "MatchA", "Seat12", false, null, "Double Booking Detected"));
        demoResults.add(new BookingResult("Fan003", "MatchB", "Seat01", true, "TX12346", null));

        SimulatorView view = new SimulatorView();
        view.display(demoResults);
    }
}
