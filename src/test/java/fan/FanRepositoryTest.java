package fan;

import model.fan.Fan;
import repository.FanRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FanRepositoryTest {

        @Test
        void testFindById() {

                FanRepository repo = new FanRepository();

                repo.addFan(new Fan("F001", "Duy", "duy@gmail.com", "0912345678", 2004));

                Fan fan = repo.findById("F001");

                assertNotNull(fan);
        }

        @Test
        void testFindByCondition() {

                FanRepository repo = new FanRepository();

                repo.addFan(new Fan("F001", "A", "a@gmail.com", "1", 2000));
                repo.addFan(new Fan("F002", "B", "b@yahoo.com", "2", 2001));

                ArrayList<Fan> result = repo.findByCondition(x -> x.getEmail().contains("gmail"));

                assertNotNull(result);
        }
}