package controller;

import model.fan.Fan;
import model.ticket.Ticket;
import model.transaction.Transaction;
import repository.FanRepository;
import repository.TicketRepository;
import repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * T5 – FanController
 * Handles: register, login, getMyTickets, updateProfile
 */
public class FanController {

    private final FanRepository fanRepo;
    private final TicketRepository ticketRepo;
    private final TransactionRepository transactionRepo;

    public FanController() {
        this.fanRepo         = new FanRepository();
        this.ticketRepo      = new TicketRepository(System.getProperty("user.dir") + "/data/tickets.csv");
        this.transactionRepo = new TransactionRepository();
    }

    // ─────────────────────────────────────────────
    //  REGISTER
    // ─────────────────────────────────────────────
    /**
     * Register a new Fan.
     * @return the created Fan, or null if email already exists / invalid input
     */
    public Fan register(String name, String email, String phone, int birthYear, String password) {
        // Validate
        if (name == null || name.isBlank())     { System.out.println("[ERROR] Name cannot be empty."); return null; }
        if (email == null || !email.contains("@")) { System.out.println("[ERROR] Invalid email."); return null; }
        if (phone == null || phone.isBlank())   { System.out.println("[ERROR] Phone cannot be empty."); return null; }
        if (birthYear < 1900 || birthYear > 2010) { System.out.println("[ERROR] Invalid birth year."); return null; }
        if (password == null || password.length() < 4) { System.out.println("[ERROR] Password must be at least 4 characters."); return null; }

        // Check duplicate email
        List<Fan> all = fanRepo.getAllFans();
        for (Fan f : all) {
            if (f.getEmail().equalsIgnoreCase(email)) {
                System.out.println("[ERROR] Email already registered.");
                return null;
            }
        }

        // Generate ID
        String newId = generateNextFanId(all);
        Fan newFan = new Fan(newId, name.trim(), email.trim(), phone.trim(), birthYear, password);
        fanRepo.addFan(newFan);
        System.out.println("[OK] Registration successful! Your Fan ID: " + newId);
        return newFan;
    }

    // ─────────────────────────────────────────────
    //  LOGIN
    // ─────────────────────────────────────────────
    /**
     * Authenticate by email + password.
     * @return Fan object if credentials match, null otherwise
     */
    public Fan login(String email, String password) {
        if (email == null || password == null) return null;

        for (Fan f : fanRepo.getAllFans()) {
            if (f.getEmail().equalsIgnoreCase(email.trim())
                    && f.getPassword().equals(password)) {
                System.out.println("[OK] Welcome back, " + f.getName() + "!");
                return f;
            }
        }
        System.out.println("[ERROR] Invalid email or password.");
        return null;
    }

    // ─────────────────────────────────────────────
    //  GET MY TICKETS
    // ─────────────────────────────────────────────
    /**
     * Get all tickets belonging to a fan (via transactions).
     */
    public List<Ticket> getMyTickets(String fanId) {
        List<Ticket> myTickets = new ArrayList<>();
        List<Transaction> myTransactions = transactionRepo.findByFanId(fanId);

        for (Transaction tr : myTransactions) {
            if (tr.getStatus() == Transaction.Status.CANCELLED) continue;
            Ticket t = ticketRepo.findById(tr.getTicketId());
            if (t != null) myTickets.add(t);
        }
        return myTickets;
    }

    // ─────────────────────────────────────────────
    //  UPDATE PROFILE
    // ─────────────────────────────────────────────
    public boolean updateProfile(Fan updatedFan) {
        Fan existing = fanRepo.findById(updatedFan.getId());
        if (existing == null) {
            System.out.println("[ERROR] Fan not found.");
            return false;
        }
        fanRepo.updateFan(updatedFan);
        System.out.println("[OK] Profile updated.");
        return true;
    }

    // ─────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────
    public Fan findById(String fanId) {
        return fanRepo.findById(fanId);
    }

    public List<Fan> getAllFans() {
        return fanRepo.getAllFans();
    }

    private String generateNextFanId(List<Fan> fans) {
        int max = 0;
        for (Fan f : fans) {
            String num = f.getId().replaceAll("[^0-9]", "");
            try { max = Math.max(max, Integer.parseInt(num)); } catch (Exception ignored) {}
        }
        return "FAN" + (max + 1);
    }
}
