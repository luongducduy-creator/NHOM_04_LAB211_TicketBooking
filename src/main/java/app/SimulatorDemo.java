package app;

import controller.SimulatorController;
import controller.StadiumController;
import controller.BookingController;
import view.SimulatorView;

import java.util.List;

/**
 * Demo class to run the concurrent seat‑booking simulation and display the result
 * using {@link SimulatorView}. This class is ONLY for demonstration / testing –
 * it does NOT alter any existing application logic.
 */
public class SimulatorDemo {
    public static void main(String[] args) {
        // 1. Create the real BookingController (it reads CSV files under data/)
        StadiumController stadiumCtrl = new StadiumController();
        BookingController bookingCtrl = new BookingController(stadiumCtrl);

        // 2. Initialise the simulator with the booking controller
        SimulatorController simulator = new SimulatorController(bookingCtrl);

        // 3. Prepare sample data – make sure these IDs exist in the CSV files
        List<String> fanIds   = List.of("FAN50001", "FAN50002", "FAN50003");
        String matchId        = "M1"; // a match present in matches.csv
        List<String> seatIds  = List.of("SEAT22", "SEAT34", "SEAT42"); // available seats
        int threads = fanIds.size(); // one thread per fan

        // 4. Run the simulation (CountDownLatch + ExecutorService inside)
        var results = simulator.runSimulation(fanIds, matchId, seatIds, threads);

        // 5. Display the ASCII table using SimulatorView
        new SimulatorView().display(results);
    }
}
