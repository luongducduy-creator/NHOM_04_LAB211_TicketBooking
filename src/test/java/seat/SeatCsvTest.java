package seat;

import model.seat.Seat; // <-- Bắt buộc phải có dòng này để import Seat
import org.junit.jupiter.api.Test; // Hoặc org.junit.Test tùy bản JUnit bạn dùng
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeatCsvTest {

    @Test
    public void testParseCsvLine() {
        String csvLine = "SEAT001,SEC1,A,01,AVAILABLE";
        Seat seat = Seat.fromCsvLine(csvLine);

        assertEquals("SEAT001", seat.getSeatId());
        assertEquals("SEC1", seat.getSectionId());
        assertEquals("A", seat.getRow());
        assertEquals("01", seat.getNumber());
        assertEquals("AVAILABLE", seat.getStatus());
        System.out.println("PASS testParseCsvLine");
    }

    @Test
    public void testSerializeCsvLine() {
        Seat seat = new Seat("SEAT002", "SEC2", "B", "15", "AVAILABLE");
        String csvLine = seat.toCsvLine();

        assertEquals("SEAT002,SEC2,B,15,AVAILABLE", csvLine);
    }

    @Test
    public void testRoundTripParseSerialize() {
        String originalCsv = "SEAT003,SEC3,C,20,AVAILABLE";
        Seat seat = Seat.fromCsvLine(originalCsv);
        String serialized = seat.toCsvLine();

        assertEquals(originalCsv, serialized);
        
    }
}
