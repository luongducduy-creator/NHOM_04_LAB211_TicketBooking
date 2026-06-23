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
                tempFile.deleteOnExit();
                repo = new FanRepository(tempFile.getAbsolutePath());
        }

        @Test
        void testAddFan() {
                Fan fan = new Fan("F001", "Duy", "duy@gmail.com", "0912345678", 2004);

                repo.addFan(fan);

                assertEquals(1, repo.getAllFans().size());
        }

        @Test
        void testGetAllFans() {
                repo.addFan(new Fan("F001", "A", "a@gmail.com", "1", 2000));
                repo.addFan(new Fan("F002", "B", "b@yahoo.com", "2", 2001));

                ArrayList<Fan> fans = repo.getAllFans();

                assertEquals(2, fans.size());
        }

        @Test
        void testFindById() {
                repo.addFan(new Fan("F001", "Duy", "duy@gmail.com", "0912345678", 2004));

                Fan fan = repo.findById("F001");

                assertNotNull(fan);
                assertEquals("F001", fan.getId());
                assertEquals("Duy", fan.getName());
        }

        @Test
        void testUpdateFan() {
                repo.addFan(new Fan("F001", "Duy", "duy@gmail.com", "0912345678", 2004));

                Fan updatedFan = new Fan(
                                "F001",
                                "David",
                                "david@gmail.com",
                                "0999999999",
                                2005);

                repo.updateFan(updatedFan);

                Fan fan = repo.findById("F001");

                assertNotNull(fan);
                assertEquals("David", fan.getName());
                assertEquals("david@gmail.com", fan.getEmail());
                assertEquals("0999999999", fan.getPhone());
                assertEquals(2005, fan.getBirthYear());
        }

        @Test
        void testDeleteFan() {
                repo.addFan(new Fan("F001", "Duy", "duy@gmail.com", "0912345678", 2004));

                repo.deleteFan("F001");

                assertNull(repo.findById("F001"));
                assertEquals(0, repo.getAllFans().size());
        }

        @Test
        void testFindByCondition() {
                repo.addFan(new Fan("F001", "A", "a@gmail.com", "1", 2000));
                repo.addFan(new Fan("F002", "B", "b@yahoo.com", "2", 2001));
                repo.addFan(new Fan("F003", "C", "c@gmail.com", "3", 2002));

                ArrayList<Fan> result = repo.findByCondition(f -> f.getEmail().contains("gmail"));

                assertEquals(2, result.size());
                assertEquals("F001", result.get(0).getId());
                assertEquals("F003", result.get(1).getId());
        }

        @Test
        void testFindCondition() {
                repo.addFan(new Fan("F001", "A", "a@gmail.com", "1", 2000));
                repo.addFan(new Fan("F002", "B", "b@yahoo.com", "2", 2001));
                repo.addFan(new Fan("F003", "C", "c@gmail.com", "3", 2002));

                ArrayList<Fan> result = repo.findByCondition(f -> f.getEmail().contains("khoa@gmail"));

                assertEquals(0, result.size());
        }
}