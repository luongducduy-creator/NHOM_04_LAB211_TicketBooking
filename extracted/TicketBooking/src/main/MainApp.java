package main;

import model.Ticket;
import model.Transaction;
import service.TicketService;
import service.TransactionService;

public class MainApp {
    public static void main(String[] args) {
        // Khởi tạo service với đường dẫn CSV
        TicketService ticketService = new TicketService("data/tickets.csv");
        TransactionService transactionService = new TransactionService("data/transactions.csv");

        // Đặt vé
        Ticket ticket = ticketService.addTicket("T001", "M001", "F001", "VIP", 200.0);
        System.out.println("Ticket booked: " + ticket.getId());

        // Thanh toán vé
        Transaction transaction = transactionService.addTransaction(
                "TR001", "T001", "F001", "2024-06-11", 200.0
        );
        System.out.println("Transaction completed: " + transaction.getId());
    }
}
