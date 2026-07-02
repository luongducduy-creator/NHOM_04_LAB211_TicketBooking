package repository;

import model.Ticket;
import java.util.List;

public class TicketTest {
    public static void main(String[] args) {
        TicketRepository repo = new TicketRepository("data/tickets.csv");

        // CREATE
        Ticket t1 = new Ticket("T001", "M001", "S001", "VIP", 150.0);
        repo.saveAll(List.of(t1));

        // READ
        List<Ticket> all = repo.findAll();
        System.out.println("Total tickets: " + all.size());

        // UPDATE
        Ticket t2 = all.get(0);
        t2.setPrice(200.0);
        repo.saveAll(all);

        // DELETE
        all.remove(0);
        repo.saveAll(all);

        System.out.println("CRUD test completed!");
    }
}
