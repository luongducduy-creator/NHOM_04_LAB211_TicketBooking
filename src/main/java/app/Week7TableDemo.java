package app;

import controller.BookingController;
import controller.SimulatorController;
import controller.StadiumController;
import repository.InvoiceRepository;
import repository.MatchRepository;
import repository.SeatRepository;
import repository.SectionRepository;
import repository.StadiumRepository;
import repository.TicketRepository;
import repository.TransactionRepository;
import view.SimulatorView;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Safe live demo for Week 7. All CSV files are created in a temporary folder.
 */
public class Week7TableDemo {
    public static void main(String[] args) throws Exception {
        int fanCount = parseFanCount(args);
        Map<String, List<SimulatorController.BookingResult>> comparison = new LinkedHashMap<>();

        PrintStream normalOutput = System.out;
        try (PrintStream quietOutput = new PrintStream(OutputStream.nullOutputStream())) {
            for (SimulatorController.SyncMechanism mechanism : SimulatorController.SyncMechanism.values()) {
                BookingController bookingController = createBookingController();
                SimulatorController simulator = new SimulatorController(bookingController);
                List<String> fanIds = IntStream.rangeClosed(1, fanCount)
                        .mapToObj(number -> "FAN" + number)
                        .toList();
                List<String> seatIds = Collections.nCopies(fanCount, "SEAT1");

                System.setOut(quietOutput);
                List<SimulatorController.BookingResult> results = simulator.runSimulation(
                        fanIds, "M1", seatIds, fanCount, mechanism);
                System.setOut(normalOutput);
                comparison.put(mechanism.name(), results);
            }
        } finally {
            System.setOut(normalOutput);
        }

        new SimulatorView().displayWeek7Comparison(comparison);
    }

    private static int parseFanCount(String[] args) {
        if (args == null || args.length == 0) {
            return 8;
        }
        try {
            int value = Integer.parseInt(args[0]);
            return Math.max(2, Math.min(value, 50));
        } catch (NumberFormatException ignored) {
            return 8;
        }
    }

    private static BookingController createBookingController() throws Exception {
        Path dataDir = Files.createTempDirectory("week7-table-demo-").resolve("data");
        Files.createDirectories(dataDir);
        Files.writeString(dataDir.resolve("seats.csv"),
                "seatId,sectionId,row,number,status,version" + System.lineSeparator()
                        + "SEAT1,SEC1,1,1,AVAILABLE,0" + System.lineSeparator());
        Files.writeString(dataDir.resolve("sections.csv"),
                "sectionId,stadiumId,name,type" + System.lineSeparator()
                        + "SEC1,ST1,VIP Area,VIP" + System.lineSeparator());
        Files.writeString(dataDir.resolve("matches.csv"),
                "matchId,homeTeam,awayTeam,date,stadiumId" + System.lineSeparator()
                        + "M1,Viet Nam,Thai Lan,2026-07-21,ST1" + System.lineSeparator());
        Files.writeString(dataDir.resolve("stadiums.csv"),
                "stadiumId,name,location,capacity" + System.lineSeparator()
                        + "ST1,National Stadium,Ha Noi,40000" + System.lineSeparator());
        Files.writeString(dataDir.resolve("tickets.csv"), "");
        Files.writeString(dataDir.resolve("transactions.csv"), "");

        SeatRepository seatRepository = new SeatRepository(dataDir.resolve("seats.csv").toString());
        StadiumController stadiumController = new StadiumController(
                new StadiumRepository(dataDir.resolve("stadiums.csv").toString()),
                new SectionRepository(dataDir.resolve("sections.csv").toString()),
                seatRepository,
                new MatchRepository(dataDir.resolve("matches.csv").toString()));
        return new BookingController(
                stadiumController,
                new TicketRepository(dataDir.resolve("tickets.csv").toString()),
                new TransactionRepository(dataDir.resolve("transactions.csv").toString()),
                new InvoiceRepository(dataDir.resolve("invoices.csv").toString()));
    }
}
