package controller;

import model.match.Match;
import model.seat.Seat;
import model.seat.Section;
import model.ticket.Ticket;
import model.ticket.TicketStatus;
import model.transaction.Transaction;
import repository.TicketRepository;
import repository.TransactionRepository;
import repository.InvoiceRepository;
import model.invoice.Invoice;

import java.util.List;
import java.time.LocalDateTime;

/**
 * T5 – BookingController (NO_LOCK strategy – single-thread)
 * Handles: bookSeat, cancelBooking, getMyTransactions
 */
public class BookingController {

    private final TicketRepository ticketRepo;
    private final TransactionRepository transactionRepo;
    private final InvoiceRepository invoiceRepo;
    private final StadiumController stadiumCtrl;

    public BookingController(StadiumController stadiumCtrl) {
        this(stadiumCtrl,
                new TicketRepository(System.getProperty("user.dir") + "/data/tickets.csv"),
                new TransactionRepository());
    }

    public BookingController(StadiumController stadiumCtrl,
            TicketRepository ticketRepo,
            TransactionRepository transactionRepo) {
        this.ticketRepo = ticketRepo;
        this.transactionRepo = transactionRepo;
        this.invoiceRepo = new InvoiceRepository();
        this.stadiumCtrl = stadiumCtrl;
    }

    public TicketRepository getTicketRepo() {
        return ticketRepo;
    }

    public TransactionRepository getTransactionRepo() {
        return transactionRepo;
    }

    // ─────────────────────────────────────────────
    // BOOK A SEAT (NO_LOCK baseline)
    // ─────────────────────────────────────────────
    /**
     * Book a seat for a match.
     * Steps:
     * 1. Verify seat exists and is AVAILABLE
     * 2. Mark seat as SOLD
     * 3. Determine ticket type and price from section
     * 4. Create Ticket (SOLD) and persist
     * 5. Create Transaction and persist
     *
     * @return the created Transaction, or null on failure
     */
    public Transaction bookSeat(String fanId, String matchId, String seatId,
            Transaction.PaymentMethod paymentMethod) {
        // 1. Get seat
        Seat seat = stadiumCtrl.getSeatById(seatId);
        if (seat == null) {
            System.out.println("[ERROR] Seat not found: " + seatId);
            return null;
        }
        if (!"AVAILABLE".equalsIgnoreCase(seat.getStatus())) {
            System.out.println("[ERROR] Seat " + seatId + " is not available.");
            return null;
        }

        // 2. Get match
        Match match = stadiumCtrl.getMatchById(matchId);
        if (match == null) {
            System.out.println("[ERROR] Match not found: " + matchId);
            return null;
        }

        // 3. Determine section and price
        Section section = stadiumCtrl.getSectionById(seat.getSectionId());
        String seatType = (section != null) ? section.getType().name() : "NORMAL";
        double price = seatType.equalsIgnoreCase("VIP") ? 800_000.0 : 300_000.0;

        // Check if ticket is already sold for this match
        List<Ticket> allTickets = ticketRepo.findAll();
        for (Ticket t : allTickets) {
            if (t.getMatchId().equals(matchId) && t.getSeatId().equalsIgnoreCase(seatId)
                    && t.getStatus() == TicketStatus.SOLD) {
                System.out.println("[ERROR] Seat " + seatId + " is already booked for this match.");
                return null;
            }
        }

        // 4. Mark seat as SOLD (NO_LOCK – single-thread safe)
        boolean marked = stadiumCtrl.markSeatBooked(seatId);
        if (!marked) {
            System.out.println("[ERROR] Seat is no longer available (concurrency issue).");
            return null;
        }

        // 5. Create and persist Ticket
        String ticketId = generateNextTicketId();
        Ticket ticket = new Ticket(ticketId, matchId, seatId, seatType, price, match.getDate(), TicketStatus.SOLD);
        ticketRepo.addTicket(ticket);

        // 6. Create and persist Transaction
        String transId = transactionRepo.generateNextId();
        Transaction transaction = new Transaction(transId, ticketId, fanId, price, paymentMethod,
                Transaction.Status.PENDING);
        transactionRepo.add(transaction);
        // Auto-confirm any pending transactions older than 3 days
        autoConfirmPendingTransactions();
        String invId = invoiceRepo.generateNextInvoiceId();
        Invoice invoice = new Invoice(invId, ticketId, price, match.getDate());
        invoiceRepo.addInvoice(invoice);

        System.out.println("[OK] Booking successful! Ticket ID: " + ticketId + "  |  Transaction: " + transId
                + "  |  Invoice: " + invId);
        return transaction;
    }

    // ─────────────────────────────────────────────
    // CANCEL BOOKING
    // ─────────────────────────────────────────────
    /**
     * Cancel a booking identified by transactionId.
     * Only the fan who owns the transaction can cancel it.
     */
    public boolean cancelBooking(String transactionId, String fanId) {
        List<Transaction> all = transactionRepo.findAll();
        Transaction target = null;
        for (Transaction t : all) {
            if (t.getTransactionId().equalsIgnoreCase(transactionId)) {
                target = t;
                break;
            }
        }
        if (target == null) {
            System.out.println("[ERROR] Transaction not found: " + transactionId);
            return false;
        }
        if (!target.getFanId().equalsIgnoreCase(fanId)) {
            System.out.println("[ERROR] You can only cancel your own bookings.");
            return false;
        }
        if (target.getStatus() == Transaction.Status.CANCELLED) {
            System.out.println("[ERROR] This booking is already cancelled.");
            return false;
        }

        // Mark ticket as CANCELLED
        Ticket ticket = ticketRepo.findById(target.getTicketId());
        if (ticket != null) {
            ticket.setStatus(TicketStatus.CANCELLED);
            // TicketRepository rewrites the whole file on next access – use removeTicket
            // approach
            ticketRepo.removeTicket(ticket.getTicketId());
            Ticket cancelled = new Ticket(ticket.getTicketId(), ticket.getMatchId(),
                    ticket.getSeatId(), ticket.getSeatType(), ticket.getPrice(),
                    ticket.getDate(), TicketStatus.CANCELLED);
            ticketRepo.addTicket(cancelled);

            // Release seat
            stadiumCtrl.releaseSeat(ticket.getSeatId());
        }

        // Mark transaction as CANCELLED
        target.setStatus(Transaction.Status.CANCELLED);
        transactionRepo.saveAll(all);

        System.out.println("[OK] Booking cancelled. Transaction: " + transactionId);
        return true;
    }

    // ─────────────────────────────────────────────
    // QUERIES
    // ─────────────────────────────────────────────
    public List<Transaction> getMyTransactions(String fanId) {
        return transactionRepo.findByFanId(fanId);
    }

    public Ticket getTicketById(String ticketId) {
        return ticketRepo.findById(ticketId);
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private String generateNextTicketId() {
        return ticketRepo.generateNextTicketId();
    }

    // ─────────────────────────────────────────────
    // STAFF OPERATIONS
    // ─────────────────────────────────────────────
    /**
     * Retrieve all pending transactions for staff review.
     */
    public java.util.List<Transaction> getPendingTransactions() {
        java.util.List<Transaction> all = transactionRepo.findAll();
        java.util.List<Transaction> pending = new java.util.ArrayList<>();
        for (Transaction t : all) {
            if (t.getStatus() == Transaction.Status.PENDING) {
                pending.add(t);
            }
        }
        return pending;
    }

    /**
     * Staff confirms a pending transaction, changing its status to SUCCESS.
     */
    public boolean staffConfirmTransaction(String transactionId) {
        java.util.List<Transaction> all = transactionRepo.findAll();
        boolean found = false;
        for (Transaction t : all) {
            if (t.getTransactionId().equalsIgnoreCase(transactionId)) {
                if (t.getStatus() != Transaction.Status.PENDING) {
                    System.out.println("[ERROR] Transaction is not pending: " + transactionId);
                    return false;
                }
                t.setStatus(Transaction.Status.SUCCESS);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("[ERROR] Transaction not found: " + transactionId);
            return false;
        }
        transactionRepo.saveAll(all);
        System.out.println("[OK] Staff confirmed transaction: " + transactionId);
        return true;
    }

    // Additional helper to auto-confirm pending transactions older than 3 days
    public void autoConfirmPendingTransactions() {
        List<Transaction> all = transactionRepo.findAll();
        boolean changed = false;
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (Transaction t : all) {
            if (t.getStatus() == Transaction.Status.PENDING) {
                try {
                    java.time.LocalDateTime created = java.time.LocalDateTime.parse(t.getCreatedAt());
                    if (created.plusDays(3).isBefore(now) || created.plusDays(3).isEqual(now)) {
                        t.setStatus(Transaction.Status.SUCCESS);
                        changed = true;
                    }
                } catch (Exception e) {
                    // ignore parse errors
                }
            }
        }
        if (changed) {
            transactionRepo.saveAll(all);
            System.out.println("[INFO] Auto-confirmed pending transactions older than 3 days.");
        }
    }

}
