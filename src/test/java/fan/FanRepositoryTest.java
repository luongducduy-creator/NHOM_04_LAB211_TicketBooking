package fan;

import model.fan.Fan;
import repository.FanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FanRepositoryTest {

        private FanRepository repo;
        private File tempFile;

        @BeforeEach
        void setUp() throws Exception {
                tempFile = File.createTempFile("test_fans_", ".csv");
                tempFile.deleteOnExit(); // tự xoá sau khi test
                repo = new FanRepository(tempFile.getAbsolutePath());
        }

        @Test
        void testFindById() {

                repo.addFan(new Fan("F001", "Duy", "duy@gmail.com", "0912345678", 2004));

                Fan fan = repo.findById("F001");

                assertNotNull(fan);
                assertEquals("F001", fan.getId());
        }

        @Test
        void testFindByCondition() {

                repo.addFan(new Fan("F001", "A", "a@gmail.com", "1", 2000));
                repo.addFan(new Fan("F002", "B", "b@yahoo.com", "2", 2001));

                ArrayList<Fan> result = repo.findByCondition(x -> x.getEmail().contains("gmail"));

                assertEquals(1, result.size());
        }
}