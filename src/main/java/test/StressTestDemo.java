package test;

import controller.SimulatorController.BookingResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import view.SimulatorView;

public class StressTestDemo {

    // Cấu hình số lượng Request và Thread chạy đồng thời
    private static final int TOTAL_REQUESTS = 1000;
    private static final int THREAD_POOL_SIZE = 100;

    public static void main(String[] args) throws InterruptedException {
        System.out.println(">>> STARTING STRESS TEST SIMULATION...");

        SimulatorView view = new SimulatorView();
        
        // Danh sách chứa kết quả (Thread-safe List để nhiều thread cùng ghi vào không bị lỗi)
        List<BookingResult> results = Collections.synchronizedList(new ArrayList<>());

        // Công cụ điều phối Thread Pool và đồng bộ thời gian xuất phát
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);

        long startTime = System.currentTimeMillis();

        // Giả lập 1,000 requests bắn vào hệ thống đồng thời
        for (int i = 1; i <= TOTAL_REQUESTS; i++) {
            final int requestId = i;
            executor.submit(() -> {
                try {
                    // TODO: Khi Bảo & Duy xong Controller, mình sẽ gọi hàm đặt vé thật ở đây:
                    // BookingResult result = controller.bookTicket(...);
                    
                    // --- GIẢ LẬP KẾT QUẢ ĐỂ TEST UI SUMMARY ---
                    boolean isSuccess = (requestId % 3 != 0); // Giả lập: 66% thành công, 33% thất bại
                    String fanId = String.format("Fan%04d", requestId);
                    String seatId = "Seat" + (requestId % 50); // Cạnh tranh 50 ghế
                    
                    BookingResult result = new BookingResult(
                        fanId, 
                        "Match_VN_THAI", 
                        seatId, 
                        isSuccess, 
                        isSuccess ? "TX_" + requestId : null, 
                        isSuccess ? null : "Seat already booked"
                    );
                    
                    results.add(result);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Chờ tất cả 1,000 requests hoàn thành
        latch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTimeTimeMs = endTime - startTime;

        // In bảng tổng hợp Báo cáo hiệu năng (Summary Report)
        view.displaySummary("OPTIMISTIC_LOCK (MOCK)", THREAD_POOL_SIZE, totalTimeTimeMs, results);
    }
}