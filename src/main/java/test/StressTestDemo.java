package test;

import controller.BookingController;
import controller.SimulatorController;
import controller.SimulatorController.BookingResult;
import controller.SimulatorController.SyncMechanism;
import controller.StadiumController;
import java.util.ArrayList;
import java.util.List;
import view.SimulatorView;

public class StressTestDemo {

    // Cấu hình tham số Stress Test
    private static final int TOTAL_REQUESTS = 1000;  // 1,000 Yêu cầu đặt vé
    private static final int THREAD_POOL_SIZE = 100; // 100 Threads chạy đồng thời
    private static final String MATCH_ID = "M1";      // Mã trận đấu hợp lệ trong DB/CSV

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println(">>> STARTING REAL STRESS TEST (" + TOTAL_REQUESTS + " REQUESTS - " + THREAD_POOL_SIZE + " THREADS) <<<");
        System.out.println("==================================================================");

        // 1. Khởi tạo các Controller & View từ hệ thống thật
        StadiumController stadiumController = new StadiumController();
        BookingController bookingController = new BookingController(stadiumController);
        SimulatorController simulatorController = new SimulatorController(bookingController);
        SimulatorView view = new SimulatorView();

        // 2. Chạy Stress Test lần lượt cho 3 cơ chế đồng bộ
        runRealTest(simulatorController, view, SyncMechanism.SYNCHRONIZED);
        runRealTest(simulatorController, view, SyncMechanism.FILE_LOCK);
        runRealTest(simulatorController, view, SyncMechanism.OPTIMISTIC);

        System.out.println("\n==================================================================");
        System.out.println(">>> ALL STRESS TESTS COMPLETED SUCCESSFULLY! <<<");
        System.out.println("==================================================================");
    }

    private static void runRealTest(SimulatorController simulatorController, 
                                    SimulatorView view, 
                                    SyncMechanism mechanism) {

        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> Running Mechanism: " + mechanism.name() + " ...");
        System.out.println("------------------------------------------------------------------");

        // Chuẩn bị dữ liệu thật: 1,000 Fan tranh chấp khoảng 200 ghế thật (SEAT100 -> SEAT299)
        List<String> fanIds = new ArrayList<>();
        List<String> seatIds = new ArrayList<>();

        for (int i = 1; i <= TOTAL_REQUESTS; i++) {
            fanIds.add(String.format("FAN%04d", i));
            // Tạo dải ghế chuẩn từ SEAT100 đến SEAT299 để có cạnh tranh đa luồng
            seatIds.add("SEAT" + (100 + (i % 200))); 
        }

        long startTime = System.currentTimeMillis();

        // GỌI HÀM ĐẶT VÉ ĐA LUỒNG THẬT TỪ SIMULATOR CONTROLLER
        List<BookingResult> results = simulatorController.runSimulation(
                fanIds, 
                MATCH_ID, 
                seatIds, 
                THREAD_POOL_SIZE, 
                mechanism
        );

        long endTime = System.currentTimeMillis();
        long totalTimeMs = endTime - startTime;

        // In bảng báo cáo Summary chuẩn UI đẹp mắt
        view.displaySummary(mechanism.name(), THREAD_POOL_SIZE, totalTimeMs, results);
    }
}