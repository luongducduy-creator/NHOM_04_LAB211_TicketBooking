package com.nhom04.ticketbooking.service;

import com.nhom04.ticketbooking.model.transaction.Transaction;
import com.nhom04.ticketbooking.model.transaction.TransactionStatus;
import com.nhom04.ticketbooking.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {
    private final TransactionRepository transactionRepository;

    // Constructor nhận filePath
    public TransactionService(String filePath) {
        this.transactionRepository = new TransactionRepository(filePath);
    }

    // Thêm giao dịch mới (5 tham số: id, ticketId, fanId, date, amount)
    public void addTransaction(String id, String ticketId, String fanId, String date, double amount) {
        Transaction transaction = new Transaction(id, ticketId, fanId, date, amount);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDateTime(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    // Tìm giao dịch theo ID
    public Transaction findById(String id) {
        return transactionRepository.getById(id);
    }

    // Lấy tất cả giao dịch
    public List<Transaction> findAll() {
        return transactionRepository.getAll();
    }

    // Cập nhật giao dịch
    public void updateTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
