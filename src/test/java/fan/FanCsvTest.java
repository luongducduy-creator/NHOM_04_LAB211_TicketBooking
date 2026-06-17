package fan;

import model.fan.Fan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FanCsvTest {

    @Test
    void testToString() {

        Fan fan = new Fan(
                "F001",
                "Duy",
                "duy@gmail.com",
                "0912345678",
                2004);

        assertEquals(
                "F001,Duy,duy@gmail.com,0912345678,2004",
                fan.toString());
    }
}