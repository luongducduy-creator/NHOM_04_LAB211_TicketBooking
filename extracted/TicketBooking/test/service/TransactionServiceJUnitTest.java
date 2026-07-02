package service;

import model.Transaction;
import model.TransactionStatus;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class TransactionServiceJUnitTest {
    private TransactionService service;

    @Before
    public void setUp() {
        // ✅ Khởi tạo service với file CSV
        service = new TransactionService("data/transactions.csv");
    }

    @Test
    public void testAddTransaction() {
        Transaction t = service.addTransaction("TR001", "T001", "F001", "2024-06-11", 200.0);
        assertNotNull(t);
        assertEquals("TR001", t.getId());
        assertEquals("T001", t.getTicketId());
        assertEquals("F001", t.getFanId());
        assertEquals(200.0, t.getAmount(), 0.01);
    }

    @Test
    public void testFindAllTransactions() {
        List<Transaction> all = service.findAll();
        assertNotNull(all);
    }

    @Test
    public void testUpdateTransactionStatus() {
        Transaction t = new Transaction("TR002", "T002", "F002", 150.0,
                LocalDateTime.now(), TransactionStatus.PENDING);
        service.saveTransaction(t);

        t.setStatus(TransactionStatus.COMPLETED);
        service.saveTransaction(t);

        Transaction updated = service.findById("TR002");
        assertEquals(TransactionStatus.COMPLETED, updated.getStatus());
    }
}
