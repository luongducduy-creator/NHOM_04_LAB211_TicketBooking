package seat;

import model.seat.Seat;
import repository.SeatRepository;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class SeatRepositoryPerformanceTest {
    @Test
    public void testPerformanceWithLargeData() throws IOException {
        SeatRepository repo = new SeatRepository("seats.csv");
        List<Seat> seats = new ArrayList<>();

        // Sinh ra 10.000 ghế
        for (int i = 0; i < 10000; i++) {
            seats.add(new Seat("SEAT" + i, "SEC1", "A", String.valueOf(i), "AVAILABLE"));
        }

        long start = System.currentTimeMillis();
        repo.saveAll(seats);
        List<Seat> loaded = repo.findAll();
        long end = System.currentTimeMillis();

        long duration = end - start;

        assertEquals(10000, loaded.size());
        assertTrue(duration <= 500, "Performance requirement not met! Duration: " + duration + "ms");
    }
}

