package fan;

import model.fan.Fan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FanCsvTest {

    @Test
    void testSerializeToCsv() {
        Fan fan = new Fan(
                "F001",
                "Duy",
                "duy@gmail.com",
                "0912345678",
                2004);

        assertEquals(
                "F001,Duy,duy@gmail.com,0912345678,2004,123456",
                fan.toCSV());
    }

    @Test
    void testParseFromCsv() {
        String csvLine = "F001,Duy,duy@gmail.com,0912345678,2004";
        Fan fan = Fan.fromCsvLine(csvLine);

        assertNotNull(fan);
        assertEquals("F001", fan.getId());
        assertEquals("Duy", fan.getName());
        assertEquals("duy@gmail.com", fan.getEmail());
        assertEquals("0912345678", fan.getPhone());
        assertEquals(2004, fan.getBirthYear());
    }
}