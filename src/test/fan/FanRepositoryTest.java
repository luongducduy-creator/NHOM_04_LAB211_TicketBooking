package test.fan;

import model.fan.Fan;
import repository.FanRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FanRepositoryTest {

    @Test
    void testFindById() {

        FanRepository repo =
                new FanRepository();

        Fan fan =
                repo.findById("F001");

        assertNotNull(fan);
    }

    @Test
    void testFindByCondition() {

        FanRepository repo =
                new FanRepository();

        ArrayList<Fan> result =
                repo.findByCondition(
                        x -> x.getEmail()
                                .contains("gmail"));

        assertNotNull(result);
    }
}