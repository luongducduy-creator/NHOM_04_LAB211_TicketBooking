package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService("transactions_test.csv");
    }

    @Test
    void testAddTransaction() {
        transactionService.addTransaction("TR001", "T001", "F001", "2026-06-19", 100.0);
        Transaction transaction = transactionService.findById("TR001");
        assertNotNull(transaction);
        assertEquals("T001", transaction.getTicketId());
        assertEquals("F001", transaction.getFanId());
        assertEquals(100.0, transaction.getAmount());
    }

    @Test
    void testFindById() {
        transactionService.addTransaction("TR002", "T002", "F002", "2026-06-19", 150.0);
        Transaction transaction = transactionService.findById("TR002");
        assertNotNull(transaction);
        assertEquals("T002", transaction.getTicketId());
    }

    @Test
    void testFindAll() {
        transactionService.addTransaction("TR003", "T003", "F003", "2026-06-19", 120.0);
        transactionService.addTransaction("TR004", "T004", "F004", "2026-06-19", 130.0);
        List<Transaction> transactions = transactionService.findAll();
        assertTrue(transactions.size() >= 2);
    }

    @Test
    void testUpdateTransaction() {
        transactionService.addTransaction("TR005", "T005", "F005", "2026-06-19", 200.0);
        Transaction transaction = transactionService.findById("TR005");
        transaction.setAmount(250.0);
        transactionService.updateTransaction(transaction);

        Transaction updated = transactionService.findById("TR005");
        assertEquals(250.0, updated.getAmount());
    }
}
