package controller;

import org.junit.jupiter.api.Test;
import repository.InvoiceRepository;
import repository.MatchRepository;
import repository.SeatRepository;
import repository.SectionRepository;
import repository.StadiumRepository;
import repository.TicketRepository;
import repository.TransactionRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatorControllerTest {

    @Test
    void fileLockAllowsOnlyOneBookingForSameSeatWithoutOverlapErrors() throws IOException {
        TestFixture fixture = createFixture(1);
        List<SimulatorController.BookingResult> results = runSameSeat(
                fixture.bookingController, SimulatorController.SyncMechanism.FILE_LOCK, 8);

        assertSingleSuccess(results);
        assertFalse(results.stream()
                .filter(result -> result.errorMessage != null)
                .anyMatch(result -> result.errorMessage.contains("OverlappingFileLockException")));
    }

    @Test
    void synchronizedAllowsOnlyOneBookingForSameSeat() throws IOException {
        TestFixture fixture = createFixture(1);
        assertSingleSuccess(runSameSeat(
                fixture.bookingController, SimulatorController.SyncMechanism.SYNCHRONIZED, 8));
    }

    @Test
    void optimisticAllowsOnlyOneBookingForSameSeatAndIncrementsVersion() throws IOException {
        TestFixture fixture = createFixture(1);
        assertSingleSuccess(runSameSeat(
                fixture.bookingController, SimulatorController.SyncMechanism.OPTIMISTIC, 8));

        assertEquals("BOOKED", fixture.seatRepository.findById("SEAT1").getStatus());
        assertEquals(1, fixture.seatRepository.findById("SEAT1").getVersion());
    }

    @Test
    void optimisticRejectsAStaleVersion() throws IOException {
        TestFixture fixture = createFixture(1);

        assertEquals(SeatRepository.OptimisticUpdateResult.SUCCESS,
                fixture.seatRepository.tryBookOptimistic("SEAT1", 0));
        assertEquals(SeatRepository.OptimisticUpdateResult.VERSION_CONFLICT,
                fixture.seatRepository.tryBookOptimistic("SEAT1", 0));
    }

    @Test
    void allMechanismsAllowConcurrentBookingsForDifferentSeatsWithUniqueIds() throws IOException {
        for (SimulatorController.SyncMechanism mechanism : SimulatorController.SyncMechanism.values()) {
            TestFixture fixture = createFixture(6);
            SimulatorController simulator = new SimulatorController(fixture.bookingController);
            List<String> fanIds = IntStream.rangeClosed(1, 6).mapToObj(i -> "FAN" + i).toList();
            List<String> seatIds = IntStream.rangeClosed(1, 6).mapToObj(i -> "SEAT" + i).toList();

            List<SimulatorController.BookingResult> results = simulator.runSimulation(
                    fanIds, "M1", seatIds, 6, mechanism);

            assertEquals(6, results.stream().filter(SimulatorController.BookingResult::success).count(),
                    "All different seats must succeed for " + mechanism);
            List<String> transactionIds = results.stream()
                    .map(result -> result.transactionId)
                    .toList();
            assertEquals(6, new HashSet<>(transactionIds).size(),
                    "Transaction IDs must be unique for " + mechanism);
        }
    }

    private static List<SimulatorController.BookingResult> runSameSeat(
            BookingController bookingController,
            SimulatorController.SyncMechanism mechanism,
            int threads) {
        SimulatorController simulator = new SimulatorController(bookingController);
        List<String> fanIds = IntStream.range(0, threads).mapToObj(i -> "FAN" + i).toList();
        List<String> seatIds = Collections.nCopies(threads, "SEAT1");
        return simulator.runSimulation(fanIds, "M1", seatIds, threads, mechanism);
    }

    private static void assertSingleSuccess(List<SimulatorController.BookingResult> results) {
        assertEquals(8, results.size());
        assertEquals(1, results.stream().filter(SimulatorController.BookingResult::success).count());
        assertTrue(results.stream().anyMatch(
                result -> result.success && result.transactionId != null));
    }

    private static TestFixture createFixture(int seatCount) throws IOException {
        Path dataDir = Files.createTempDirectory("ticket-booking-week7-").resolve("data");
        Files.createDirectories(dataDir);

        StringBuilder seats = new StringBuilder("seatId,sectionId,row,number,status,version")
                .append(System.lineSeparator());
        for (int i = 1; i <= seatCount; i++) {
            seats.append("SEAT").append(i).append(",SEC1,1,").append(i)
                    .append(",AVAILABLE,0").append(System.lineSeparator());
        }
        Files.writeString(dataDir.resolve("seats.csv"), seats.toString());
        Files.writeString(dataDir.resolve("sections.csv"), String.join(System.lineSeparator(),
                "sectionId,stadiumId,name,type",
                "SEC1,ST1,Main,VIP") + System.lineSeparator());
        Files.writeString(dataDir.resolve("matches.csv"), String.join(System.lineSeparator(),
                "matchId,homeTeam,awayTeam,date,stadiumId",
                "M1,TeamA,TeamB,2026-01-01,ST1") + System.lineSeparator());
        Files.writeString(dataDir.resolve("stadiums.csv"), String.join(System.lineSeparator(),
                "stadiumId,name,location,capacity",
                "ST1,Test Stadium,Test City,100") + System.lineSeparator());
        Files.writeString(dataDir.resolve("tickets.csv"), "");
        Files.writeString(dataDir.resolve("transactions.csv"), "");

        SeatRepository seatRepository = new SeatRepository(dataDir.resolve("seats.csv").toString());
        StadiumController stadiumController = new StadiumController(
                new StadiumRepository(dataDir.resolve("stadiums.csv").toString()),
                new SectionRepository(dataDir.resolve("sections.csv").toString()),
                seatRepository,
                new MatchRepository(dataDir.resolve("matches.csv").toString()));
        BookingController bookingController = new BookingController(
                stadiumController,
                new TicketRepository(dataDir.resolve("tickets.csv").toString()),
                new TransactionRepository(dataDir.resolve("transactions.csv").toString()),
                new InvoiceRepository(dataDir.resolve("invoices.csv").toString()));
        return new TestFixture(bookingController, seatRepository);
    }

    private record TestFixture(BookingController bookingController, SeatRepository seatRepository) {
    }
}
