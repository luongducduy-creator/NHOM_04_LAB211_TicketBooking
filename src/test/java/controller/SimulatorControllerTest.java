package controller;

import model.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatorControllerTest {

    @Test
    void fileLockMechanismShouldAllowOnlyOneSuccessfulBookingForSameSeat() throws IOException {
        BookingController bookingController = createBookingController();
        SimulatorController simulator = new SimulatorController(bookingController);

        List<String> fanIds = IntStream.range(0, 4).mapToObj(i -> "FAN" + i).toList();
        List<String> seatIds = Collections.nCopies(4, "SEAT1");

        List<SimulatorController.BookingResult> results = simulator.runSimulation(fanIds, "M1", seatIds, 4,
                SimulatorController.SyncMechanism.FILE_LOCK);

        assertEquals(4, results.size());
        long successCount = results.stream().filter(SimulatorController.BookingResult::success).count();
        assertEquals(1, successCount, "Only one thread should succeed when booking the same seat");
    }

    @Test
    void synchronizedMechanismShouldAllowOnlyOneSuccessfulBookingForSameSeat() throws IOException {
        BookingController bookingController = createBookingController();
        SimulatorController simulator = new SimulatorController(bookingController);

        List<String> fanIds = IntStream.range(0, 4).mapToObj(i -> "FAN" + i).toList();
        List<String> seatIds = Collections.nCopies(4, "SEAT1");

        List<SimulatorController.BookingResult> results = simulator.runSimulation(fanIds, "M1", seatIds, 4,
                SimulatorController.SyncMechanism.SYNCHRONIZED);

        assertEquals(4, results.size());
        long successCount = results.stream().filter(SimulatorController.BookingResult::success).count();
        assertEquals(1, successCount, "Only one thread should succeed when booking the same seat");
    }

    @Test
    void optimisticMechanismShouldAllowOnlyOneSuccessfulBookingForSameSeat() throws IOException {
        BookingController bookingController = createBookingController();
        SimulatorController simulator = new SimulatorController(bookingController);

        List<String> fanIds = IntStream.range(0, 4).mapToObj(i -> "FAN" + i).toList();
        List<String> seatIds = Collections.nCopies(4, "SEAT1");

        List<SimulatorController.BookingResult> results = simulator.runSimulation(fanIds, "M1", seatIds, 4,
                SimulatorController.SyncMechanism.OPTIMISTIC);

        assertEquals(4, results.size());
        long successCount = results.stream().filter(SimulatorController.BookingResult::success).count();
        assertEquals(1, successCount, "Only one thread should succeed when booking the same seat");
        assertTrue(results.stream().anyMatch(r -> r.success && r.transactionId != null));
    }

    private BookingController createBookingController() throws IOException {
        Path tempDir = Files.createTempDirectory("ticket-booking-test");
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        Files.writeString(dataDir.resolve("seats.csv"), String.join(System.lineSeparator(),
                "seatId,sectionId,row,number,status",
                "SEAT1,SEC1,1,1,AVAILABLE") + System.lineSeparator());

        Files.writeString(dataDir.resolve("sections.csv"), String.join(System.lineSeparator(),
                "sectionId,stadiumId,name,type",
                "SEC1,ST1,Main,VIP") + System.lineSeparator());

        Files.writeString(dataDir.resolve("matches.csv"), String.join(System.lineSeparator(),
                "matchId,homeTeam,awayTeam,date,stadiumId",
                "M1,TeamA,TeamB,2026-01-01,ST1") + System.lineSeparator());

        Files.writeString(dataDir.resolve("tickets.csv"), "" + System.lineSeparator());
        Files.writeString(dataDir.resolve("transactions.csv"), "" + System.lineSeparator());

        System.setProperty("user.dir", tempDir.toString());
        StadiumController stadiumController = new StadiumController();
        return new BookingController(stadiumController);
    }
}
